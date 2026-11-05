package com.geo.bridge.api.emitter.simulator.service;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.geo.bridge.api.emitter.simulator.model.EmitterSimulatorRQ;
import com.geo.bridge.api.emitter.simulator.model.EmitterSimulatorRestartRQ;
import com.geo.bridge.domain.simulator.SimulatorService;
import com.geo.bridge.domain.simulator.context.EmitterContext;
import com.geo.bridge.domain.simulator.history.HistoryService;
import com.geo.bridge.domain.simulator.history.dto.entity.History;
import com.geo.bridge.domain.simulator.integration.client.EmitterClient;
import com.geo.bridge.domain.simulator.model.CreateSimulatorClientDTO;
import com.geo.bridge.global.base.BasePointDTO;
import com.geo.bridge.global.security.SecurityHelper;
import com.geo.bridge.global.security.model.CustomUserDetails;
import com.geo.bridge.global.utils.JsonUtils;
import com.geo.bridge.global.utils.SpeedUtils;
import com.geo.bridge.global.utils.SpeedUtils.SpeedUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

/**
 * Emitter Simulator Service
 * <p>기능</p>
 * <ul>
 *     <li>{@link #startEmitterSimulator(Mono)} 시뮬레이션 시작</li>
 *     <li>{@link #stopEmitterSmiulator(String)} 시뮬레이션 정지</li>
 *     <li>{@link #restartEmitterSimulator(EmitterSimulatorRestartRQ)} 시뮬레이션 재시작</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmitterSimulatorService {
    
    private final SimulatorService simulatorService;
    private final HistoryService historyService;

    /**
     * 시뮬레이션
     * 1. 속도값 조정
     * 2. client 생성 및 Cooridnate 데이터 생성
     * 3. client context 등록
     * 4. 데이터 전송
     * @param rq
     * @return UUID전송
     */
    public Mono<String> startEmitterSimulator(Mono<EmitterSimulatorRQ> rq) {
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
                Mono<List<BasePointDTO>> coordinates = simulatorService.createSimulatorCoordinates(routeCoordinates, emitterSimulatrRQ.getSpeed());

                return saveHistory(emitterSimulatrRQ)
                    .then(Mono.zip(client, coordinates, Mono.just(emitterSimulatrRQ.getFormat()), Mono.just(emitterSimulatrRQ.getCycle()), SecurityHelper.securityHolder()));
            })
            // 3. client context 등록 및 시작
            .map(tuple -> {
                EmitterClient client = tuple.getT1();
                List<BasePointDTO> coordinates = tuple.getT2();
                String format = tuple.getT3();
                Integer cycle = tuple.getT4();
                UserDetails userDetails = tuple.getT5();

                String uuid = EmitterContext.put(userDetails.getUsername(), client, format, coordinates, cycle);
                EmitterContext.excute(userDetails.getUsername(), uuid);

                return uuid;
            });
    }

    /**
     * 실행 이력 저장 여부에 따라 시뮬레이터 설정을 저장합니다.
     * - 저장 여부가 false이면 저장하지 않습니다.
     * - 저장 여부가 true이면 로그인 사용자와 실행 설정을 이력으로 저장합니다.
     *
     * @param rq - 시뮬레이터 실행 요청
     * @return 저장 완료 시그널
     */
    private Mono<Void> saveHistory(EmitterSimulatorRQ rq) {
        if (!Boolean.TRUE.equals(rq.getHistorySaveYn())) {
            return Mono.empty();
        }

        return SecurityHelper.securityHolder()
            .flatMap(user -> {
                History history = History.builder()
                    .username(user.getUsername())
                    .title(rq.getName())
                    .path(JsonUtils.toJson(rq.getPointList()))
                    .type(rq.getType().name())
                    .host(rq.getHost())
                    .topic(rq.getTopic())
                    .hostId(rq.getHostId())
                    .hostPassword(rq.getPassword())
                    .speed(rq.getSpeed())
                    .speedUnit(rq.getSpeedUnit().name())
                    .cycle(rq.getCycle())
                    .format(rq.getFormat())
                    .parameter(JsonUtils.toJson(rq.getParameter()))
                    .build();

                return historyService.saveHistory(Mono.just(history));
            });
    }

    /**
     * 시뮬레이션 정지
     *
     * @param uuid Emitter Control UUID
     * @return 정지 및 Context 제거 완료 시그널
     */
    public Mono<Void> stopEmitterSmiulator(String uuid){
        return SecurityHelper.securityHolder()
            .map(user -> {
                EmitterContext.stop(user.getUsername(), uuid);
                return Tuples.of(user, true);
            })
            .doOnSuccess(zip -> {
                Boolean successYn = zip.getT2();
                if(successYn){
                    CustomUserDetails user = zip.getT1();
                    
                    String username = user.getUsername();
                    EmitterContext.remove(username, uuid);
                }
            })
            .then();
    }

    /**
     * 기존 시뮬레이터를 재실행합니다.
     *
     * <p>
     * 현재 로그인 사용자 기준으로 {@link EmitterContext}에 등록된 manager를 다시 실행합니다.
     * 대상 UUID가 이미 제거되었거나 존재하지 않으면 내부 Context 상태에 따라 실질적인 재실행은 일어나지 않을 수 있습니다.
     * </p>
     *
     * @param rq 재실행할 시뮬레이터 UUID 요청값
     * @return 재실행 요청 대상 UUID
     */
    public Mono<String> restartEmitterSimulator(EmitterSimulatorRestartRQ rq) {
        return SecurityHelper.securityHolder()
            .map(user -> {
                String username = user.getUsername();
                return EmitterContext.restart(username, rq.getUuid());
            });
    }

}
