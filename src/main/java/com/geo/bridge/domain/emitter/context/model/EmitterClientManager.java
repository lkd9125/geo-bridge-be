package com.geo.bridge.domain.emitter.context.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.locationtech.jts.geom.Coordinate;

import com.geo.bridge.domain.emitter.integration.client.EmitterClient;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

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

    private List<Coordinate> cooridnates;

    private String format;

    private FastBinder fastBinder;

    private Disposable disposable;

    private int cycle;

    private Map<String, Object> baseParameters;

    /**
     * HOST에 전송 시작
     */
    public void excute(){
        stop();

        log.info("Client Excute RunningTime :: {} Second", cooridnates.size() * cycle);
        this.status = EmitterClientStatus.PLAYING;
        this.disposable = Flux.fromIterable(cooridnates)
            .repeat(this.cycle - 1) // [핵심] 1회 실행 후 (cycle-1)번 더 반복 -> 총 cycle번 실행
            .delayElements(Duration.ofSeconds(1L))
            .map(cooridnate -> {
                // 파라미터 세팅
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("lat", cooridnate.getY());
                parameters.put("lon", cooridnate.getX());

                return this.bindFormat(parameters);
            })
            .flatMap(sendData -> client.send(sendData))
            .doFinally(onFinally -> {
                this.status = EmitterClientStatus.END;
            })
            .subscribe(); // TODO :: Scheduelr Boundery 추가해야 함
    }

    /**
     * HOST에 전송 정지
     */
    public void stop(){
        if (this.disposable != null && !this.disposable.isDisposed()) {
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
    private String bindFormat(Map<String, Object> parameters){
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
        public String bind(Map<String, Object> data) {
            // StringBuilder 크기를 미리 지정하여 내부 배열 Resizing 오버헤드 방지
            StringBuilder sb = new StringBuilder(estimatedLength + 32);

            for (Segment segment : segments) {
                if (segment.isVariable) {
                    // 변수면 맵에서 값을 찾아 넣음 (null이면 빈 문자열 처리 등 정책 결정)
                    Object val = data.get(segment.content);
                    sb.append(val != null ? val : ""); 
                } else {
                    // 고정 텍스트면 그냥 넣음
                    sb.append(segment.content);
                }
            }
            return sb.toString();
        }
    }

}
