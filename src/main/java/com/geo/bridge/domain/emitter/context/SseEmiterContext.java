package com.geo.bridge.domain.emitter.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import reactor.core.publisher.Sinks;

/**
 * SSE Emitter Context.
 *
 * <p>
 * 사용자(고객번호) 단위로 SSE 전송 채널({@link Sinks.Many})을 보관하고 재사용하기 위한 컨텍스트입니다.
 * </p>
 *
 * <p>기능</p>
 * <ul>
 *  <li>{@link #getSseEmiter(String)} 사용자별 Sink 조회/생성</li>
 *  <li>{@link #removeEmitter(String)} 구독자가 없을 때 Sink 정리</li>
 * </ul>
 */
public class SseEmiterContext {

    private static Map<String, Sinks.Many<String>> sseEmiterMap = new ConcurrentHashMap<>();

    /**
     * 사용자별 SSE Sink를 조회합니다. 존재하지 않으면 생성합니다.
     *
     * <p>
     * {@code multicast().onBackpressureBuffer()} 전략을 사용하여 다중 구독자에게 브로드캐스트하며,
     * 소비 속도가 느린 구독자가 있더라도 발행자 측에서 스트림이 끊기지 않도록 버퍼링합니다.
     * </p>
     *
     * @param custNo 사용자 식별자
     * @return 사용자별 SSE sink
     */
    public static Sinks.Many<String> getSseEmiter(String custNo){
        return sseEmiterMap.computeIfAbsent(custNo, id -> 
            Sinks.many().multicast().onBackpressureBuffer()
        );
    }

    /**
     * 구독자가 더 이상 없을 때 사용자 Sink를 정리합니다.
     *
     * @param custNo 사용자 식별자
     */
    public static void removeEmitter(String custNo) {
        Sinks.Many<String> sink = sseEmiterMap.get(custNo);
        if (sink != null && sink.currentSubscriberCount() == 0) {
            sseEmiterMap.remove(custNo);
        }
    }

}
