package com.geo.bridge.domain.emitter.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import reactor.core.publisher.Sinks;

public class SseEmiterContext {

    private static Map<String, Sinks.Many<String>> sseEmiterMap = new ConcurrentHashMap<>();

    public static Sinks.Many<String> getSseEmiter(String custNo){
        return sseEmiterMap.computeIfAbsent(custNo, id -> 
            Sinks.many().multicast().onBackpressureBuffer()
        );
    }

    public static void removeEmitter(String custNo) {
        Sinks.Many<String> sink = sseEmiterMap.get(custNo);
        if (sink != null && sink.currentSubscriberCount() == 0) {
            sseEmiterMap.remove(custNo);
        }
    }

}
