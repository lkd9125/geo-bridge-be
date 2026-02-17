package com.geo.bridge.domain.emitter.context.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.http.codec.ServerSentEvent;

import com.geo.bridge.domain.emitter.context.SseEmiterContext;
import com.geo.bridge.domain.emitter.integration.client.EmitterClient;
import com.geo.bridge.global.base.BasePointDTO;
import com.geo.bridge.global.utils.JsonUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * Client Manager
 * 
 * <p>변수</p>
 * <ul>
 *  <li>{@link #uuid} manager 식별번호</li>
 *  <li>{@link #status} 현재 상태</li>
 *  <li>{@link #client} emitter client </li>
 *  <li>{@link #cooridnates} 실행시킬 좌표</li>
 *  <li>{@link #format} 포맷팅</li>
 *  <li>{@link #disposable} 흐름 제어</li>
 *  <li>{@link #fastBinder} String 바인딩 객체(내부용)</li>
 *  <li>{@link #cycle} 반복 횟수</li>
 *  <li>{@link #baseParameters} 바인딩 시킬 기본 파라미터</li>
 * </ul>
 * 
 * <p>기능</p>
 * <ul>
 *  <li>{@link #excute()} Client 데이터 전송 시작</li>
 *  <li>{@link #bindFormat(Map)} 포맷팅 바인딩</li>
 *  <li>{@link #stop()} Client 데이터 전송 정지</li>
 * </ul>
 */
@Data
@Slf4j
public class EmitterClientManager {

    private String uuid = UUID.randomUUID().toString();

    private EmitterClientStatus status = EmitterClientStatus.WAIT;

    private EmitterClient client;

    private List<BasePointDTO> cooridnates;

    private String format;

    private FastBinder fastBinder;

    private Disposable disposable;

    private int cycle;

    private Map<String, String> baseParameters;

    private Integer nowSeconds = 0;

    private String custNo;

    /**
     * HOST에 전송 시작
     */
    public void excute(){
        stop();

        log.info("Client Excute RunningTime :: {} Second", cooridnates.size() * cycle);
        this.status = EmitterClientStatus.PLAYING;
        this.disposable = Flux.fromIterable(cooridnates)
            .repeat(this.cycle - 1) // 1회 실행 후 (cycle-1)번 더 반복 -> 총 cycle번 실행
            .delayElements(Duration.ofSeconds(1L))
            .doOnNext(cooridnate -> {
                // excute 이후 지난 시간 측정
                this.nowSeconds += 1;
                // sse emitter monitoring용 전송
                EmitterBody emitterBody = new EmitterBody();
                emitterBody.setUuid(this.uuid);
                emitterBody.setLat(cooridnate.getLat());
                emitterBody.setLon(cooridnate.getLon());
                emitterBody.setHeading(cooridnate.getHeading());
                emitterBody.setStatus(this.status.name());

                SseEmiterContext.getSseEmiter(custNo).tryEmitNext(JsonUtils.toJson(emitterBody));
            })
            .map(cooridnate -> {
                // 파라미터 세팅
                baseParameters.put("lat", String.valueOf(cooridnate.getLat()));
                baseParameters.put("lon", String.valueOf(cooridnate.getLon()));
                baseParameters.put("heading", String.valueOf(cooridnate.getHeading()));

                return this.bindFormat(baseParameters);
            })
            .flatMap(sendData -> client.send(sendData))
            .doFinally(onFinally -> {
                this.status = EmitterClientStatus.END;

                EmitterBody emitterBody = new EmitterBody();
                emitterBody.setUuid(this.uuid);
                emitterBody.setStatus(this.status.name());

                SseEmiterContext.getSseEmiter(custNo).tryEmitNext(JsonUtils.toJson(emitterBody));
            })
            .subscribe(); // TODO :: Scheduelr Boundery 추가해야 함
    }

    /**
     * HOST에 전송 정지
     */
    public void stop(){
        boolean disposableIsNotNull = this.disposable != null && !this.disposable.isDisposed();
        boolean isPlaying = this.status == EmitterClientStatus.PLAYING;
        if (disposableIsNotNull && isPlaying) {
            this.disposable.dispose();
            log.info("Client stopped manually.");
            
            this.status = EmitterClientStatus.END;
        }
    }

    /**
     * Format Setting
     * @param format
     */
    public void setFormat(String format){
        this.fastBinder = new FastBinder(format);
    }

    /**
     * 포맷팅 바인딩
     * @param lat 위도
     * @param lon 경도
     * @return binding 한 전송 포맷
     */
    private String bindFormat(Map<String, String> parameters){
        return fastBinder.bind(parameters);
    }

    
    @Data
    private static class FastBinder{
        // 템플릿 조각을 저장할 내부 클래스
        private static class Segment {
            boolean isVariable; // 변수인지 고정 텍스트인지
            String content;     // 텍스트 내용 혹은 변수 키값

            Segment(boolean isVariable, String content) {
                this.isVariable = isVariable;
                this.content = content;
            }
        }

        private final List<Segment> segments = new ArrayList<>();
        private final int estimatedLength; // 성능 최적화를 위한 예상 길이

        // 생성자: 여기서 템플릿 파싱을 단 한 번만 수행합니다. (가장 중요)
        public FastBinder(String template) {
            // #{name} 패턴을 찾습니다.
            Pattern pattern = Pattern.compile("#\\{([^}]+)\\}");
            Matcher matcher = pattern.matcher(template);

            int lastIndex = 0;
            int tempLength = 0;

            while (matcher.find()) {
                // 1. 변수 앞의 고정 텍스트 저장
                if (lastIndex < matcher.start()) {
                    String text = template.substring(lastIndex, matcher.start());
                    segments.add(new Segment(false, text));
                    tempLength += text.length();
                }

                // 2. 변수 키 저장 (#{key} 에서 key만 추출)
                String key = matcher.group(1);
                segments.add(new Segment(true, key));
                tempLength += 10; // 변수값의 대략적인 평균 길이 예상

                lastIndex = matcher.end();
            }

            // 3. 마지막 남은 텍스트 저장
            if (lastIndex < template.length()) {
                String text = template.substring(lastIndex);
                segments.add(new Segment(false, text));
                tempLength += text.length();
            }
            
            this.estimatedLength = tempLength;
        }

        // 실제 데이터 바인딩 (매우 빠름)
        public String bind(Map<String, String> data) {
            // StringBuilder 크기를 미리 지정하여 내부 배열 Resizing 오버헤드 방지
            StringBuilder sb = new StringBuilder(estimatedLength + 32);

            for (Segment segment : segments) {
                if (segment.isVariable) {
                    // 변수면 맵에서 값을 찾아 넣음 (null이면 빈 문자열 처리 등 정책 결정)
                    String val = data.get(segment.content);
                    sb.append(val != null ? val : ""); 
                } else {
                    // 고정 텍스트면 그냥 넣음
                    sb.append(segment.content);
                }
            }
            return sb.toString();
        }
    }

    @Data
    private static class EmitterBody{
        private String uuid;
        private Double lat;
        private Double lon;
        private Double heading;
        private String status;
    }

}
