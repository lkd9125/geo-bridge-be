package com.geo.bridge.api.emitter.client.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geo.bridge.api.emitter.client.service.EmitterClientService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/emitter/client")
public class EmitterClientController {

    private final EmitterClientService emitterClientService;

    /**
     * 시뮬레이션 좌표 모니터링
     * @return
     */
    @GetMapping(value = "/monitoring/coords", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> monitoring(){
        return emitterClientService.monitoring();
    }

}
