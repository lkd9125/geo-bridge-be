package com.geo.bridge.domain.emitter.context.model;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.locationtech.jts.geom.Coordinate;

import com.geo.bridge.domain.emitter.integration.client.EmitterClient;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

/**
 * Client Manager
 * 
 * <p>변수</p>
 * <ul>
 *  <li>{@link #uuid} manager 식별번호</li>
 *  <li>{@link #status} 현재 상태</li>
 *  <li>{@link #client} emitter client </li>
 *  <li>{@link #cooridnates} 실행시킬 좌표</li>
 *  <li>{@link #format}</li>
 * </ul>
 * 
 * <p>기능</p>
 * <ul>
 *  <li>{@link #excute()} Client 데이터 전송 시작</li>
 *  <li>{@link #bindFormat(Double, Double)} 포맷팅 바인딩</li>
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

    /**
     * HOST에 전송 시작
     */
    public void excute(){
        log.info("Client Excute RunningTime :: {} Second", cooridnates.size());
        this.status = EmitterClientStatus.PLAYING;
        
        Flux.fromIterable(cooridnates)
            .delayElements(Duration.ofSeconds(1L))
            .map(cooridnate -> this.bindFormat(cooridnate.getY(), cooridnate.getX()))
            .flatMap(sendData -> client.send(sendData))
            .doFinally(onFinally -> {
                this.status = EmitterClientStatus.END;
            })
            .subscribe(); // TODO :: Scheduelr Boundery 추가해야 함
    }

    /**
     * 포맷팅 바인딩
     * @param lat 위도
     * @param lon 경도
     * @return binding 한 전송 포맷
     */
    private String bindFormat(Double lat, Double lon){
        return "";
    }

}
