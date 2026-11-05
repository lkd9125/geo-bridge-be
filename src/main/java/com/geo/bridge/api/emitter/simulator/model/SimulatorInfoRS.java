package com.geo.bridge.api.emitter.simulator.model;

import com.geo.bridge.domain.simulator.context.model.EmitterClientStatus;

import lombok.Data;

@Data
public class SimulatorInfoRS {

    private String uuid;

    private EmitterClientStatus status;

}
