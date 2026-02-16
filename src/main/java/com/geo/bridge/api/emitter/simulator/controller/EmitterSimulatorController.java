package com.geo.bridge.api.emitter.simulator.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.geo.bridge.api.emitter.simulator.model.EmitterSimulatorRQ;
import com.geo.bridge.api.emitter.simulator.service.EmitterSimulatorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Simulator Controller
 * 
 * <p>기능</p>
 * <ul>
 *  <li>{@link #emitterSimulator(Mono)} 시뮬레이터 클라이언트 실행</li>
 * </ul>
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/emitter/simulator")
public class EmitterSimulatorController {

    private final EmitterSimulatorService emitterSimulatorService;

    /**
     * 시뮬레이터 클라이언트 실행
     * @return 실행 후 나오는 UUID 전송
     */
    @PostMapping
    public Mono<String> startEmitterSimulator(@RequestBody Mono<EmitterSimulatorRQ> rq){
        return emitterSimulatorService.startEmitterSimulator(rq);
    }

    @DeleteMapping
    public Mono<Void> startEmitterSimulator(@RequestParam("uuid") String uuid){
        return emitterSimulatorService.stopEmitterSmiulator(uuid);
    }

}
