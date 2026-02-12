package com.geo.bridge.api.emitter.simulator.service;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.geo.bridge.api.emitter.simulator.model.EmitterSimulatorRQ;
import com.geo.bridge.domain.emitter.SimulatorService;
import com.geo.bridge.domain.emitter.context.EmitterContext;
import com.geo.bridge.domain.emitter.integration.client.EmitterClient;
import com.geo.bridge.domain.emitter.model.CreateSimulatorClientDTO;
import com.geo.bridge.global.base.BasePointDTO;
import com.geo.bridge.global.security.SecurityHelper;
import com.geo.bridge.global.utils.SpeedUtils;
import com.geo.bridge.global.utils.SpeedUtils.SpeedUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Emitter Simulator Service
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmitterSimulatorService {
    
    private final SimulatorService simulatorService;

    /**
     * 시뮬레이션
     * 1. 속도값 조정
     * 2. client 생성 및 Cooridnate 데이터 생성
     * 3. client context 등록
     * 4. 데이터 전송
     * @param rq
     * @return UUID전송
     */
    public Mono<String> emitterSimulator(Mono<EmitterSimulatorRQ> rq) {
        return rq
            // 1. 속도값 조정
            .map(emitterSimulatrRQ -> {
                SpeedUnit unit = emitterSimulatrRQ.getSpeedUnit();
                Double speed = emitterSimulatrRQ.getSpeed();
                
                Double fixSpeed = SpeedUtils.convertToMs(speed, unit);

                emitterSimulatrRQ.setSpeed(fixSpeed);
                emitterSimulatrRQ.setSpeedUnit(SpeedUnit.M_S);

                return emitterSimulatrRQ;
            })
            // 2. client 생성 및 Cooridnate 데이터 생성
            .flatMap(emitterSimulatrRQ -> {
                CreateSimulatorClientDTO dto = new CreateSimulatorClientDTO();
                dto.setName(emitterSimulatrRQ.getName());
                dto.setHost(emitterSimulatrRQ.getHost());
                dto.setUsername(emitterSimulatrRQ.getHostId());
                dto.setPassword(emitterSimulatrRQ.getPassword());
                dto.setTopic(emitterSimulatrRQ.getTopic());
                dto.setType(emitterSimulatrRQ.getType());
                dto.setParameter(emitterSimulatrRQ.getParameter());

                List<Coordinate> routeCoordinates = new ArrayList<>();
                for(BasePointDTO point : emitterSimulatrRQ.getPointList()){
                    routeCoordinates.add(point.toCoordinate());
                }

                Mono<EmitterClient> client = simulatorService.createSimulatorClient(dto);
                Mono<List<Coordinate>> coordinates = simulatorService.createSimulatorCoordinates(routeCoordinates, emitterSimulatrRQ.getSpeed());

                return Mono.zip(client, coordinates, Mono.just(emitterSimulatrRQ.getFormat()), Mono.just(emitterSimulatrRQ.getCycle()), SecurityHelper.securityHolder());
            })
            // 3. client context 등록 및 시작
            .map(tuple -> {
                EmitterClient client = tuple.getT1();
                List<Coordinate> coordinates = tuple.getT2();
                String format = tuple.getT3();
                Integer cycle = tuple.getT4();
                UserDetails userDetails = tuple.getT5();

                String uuid = EmitterContext.put(userDetails.getUsername(), client, format, coordinates, cycle);
                EmitterContext.excute(userDetails.getUsername(), uuid);

                return uuid;
            });
    }


}
