package com.geo.bridge.api.emitter.client.service;

import org.springframework.stereotype.Service;

import com.geo.bridge.domain.emitter.context.SseEmiterContext;
import com.geo.bridge.global.security.SecurityHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmitterClientService {

    /**
     * 인증된 사용자에 대한 SSE 모니터링 스트림
     * @return 해당 사용자의 Sink에서 발행되는 Flux
     */
    public Flux<String> monitoring(){
        return SecurityHelper.securityHolder()
            .flatMapMany(user -> {
                String username = user.getUsername();
                Sinks.Many<String> sink = SseEmiterContext.getSseEmiter(username);
                return Flux.concat(
                        Flux.just("{\"status\":\"connected\"}"),  // 연결 직후 즉시 전송
                        sink.asFlux()
                    )
                    .doOnCancel(() -> SseEmiterContext.removeEmitter(username))
                    .doOnTerminate(() -> SseEmiterContext.removeEmitter(username));
            });
    }

}
