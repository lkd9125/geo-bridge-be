package com.geo.bridge.api.emitter.simulator.model;

import lombok.Data;

/**
 * 시뮬레이터 재실행 요청 모델.
 */
@Data
public class EmitterSimulatorRestartRQ {

    /**
     * 재실행 대상 시뮬레이터 UUID.
     */
    private String uuid;

}
