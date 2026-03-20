package com.geo.bridge.api.emitter.simulator.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.geo.bridge.api.emitter.simulator.model.EmitterSimulatorRQ;
import com.geo.bridge.api.emitter.simulator.model.EmitterSimulatorRestartRQ;
import com.geo.bridge.api.emitter.simulator.service.EmitterSimulatorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Simulator Controller
 * 
 * <p>기능</p>
 * <ul>
 *  <li>{@link #startEmitterSimulator(Mono)} 시뮬레이터 클라이언트 실행</li>
 *  <li>{@link #stopEmitterSimulator(String)} 시뮬레이터 클라이언트 종료</li>
 *  <li>{@link #restartEmitterSimulator(EmitterSimulatorRestartRQ)} 시뮬레이터 클라이언트 재실행</li>
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
     * @param rq {@link EmitterSimulatorRQ} 시뮬레이터 실행 요청
     * @return 실행 후 나오는 UUID 전송
     */
    @PostMapping
    public Mono<String> startEmitterSimulator(@RequestBody Mono<EmitterSimulatorRQ> rq){
        return emitterSimulatorService.startEmitterSimulator(rq);
    }

    /**
     * 시뮬레이션 클라이언트 종료
     * @param uuid 실행 후 나오는 UUID
     * @return 종료 완료 시그널
     */
    @DeleteMapping
    public Mono<Void> stopEmitterSimulator(@RequestParam("uuid") String uuid){
        return emitterSimulatorService.stopEmitterSmiulator(uuid);
    }
    
    /**
     * 기존 시뮬레이터 클라이언트를 재실행합니다.
     *
     * @param rq 재실행할 시뮬레이터 UUID 요청
     * @return 재실행 요청 대상 UUID
     */
    @PostMapping("/restart")
    public Mono<String> restartEmitterSimulator(@RequestBody EmitterSimulatorRestartRQ rq){
        return emitterSimulatorService.restartEmitterSimulator(rq);
    }

}
