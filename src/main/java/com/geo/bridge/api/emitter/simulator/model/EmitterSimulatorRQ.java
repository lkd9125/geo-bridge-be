package com.geo.bridge.api.emitter.simulator.model;

import java.util.List;

import com.geo.bridge.domain.emitter.integration.model.EmitterType;
import com.geo.bridge.global.base.BasePointDTO;
import com.geo.bridge.global.utils.SpeedUtils.SpeedUnit;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 시뮬레이션 RQ
 * 
 * <p>변수</p>
 * <ul>
 *     <li>{@link #name} : 클라이어트 식별 이름</li>
 *     <li>{@link #host} : 연결 호스트</li>
 *     <li>{@link #type} : 연결 타입</li>
 *     <li>{@link #topic} : 토픽</li>
 *     <li>{@link #hostId} : 아이디</li>
 *     <li>{@link #password} : 비밀번호</li>
 *     <li>{@link #pointList} : 좌표 리스트</li>
 *     <li>{@link #speed} : 속도(m/s)</li>
 *     <li>{@link #speedUnit} : 속도 단위</li>
 *     <li>{@link #cycle} : 반복주기</li>
 *     <li>{@link #format} : 전송 포맷</li>
 * </ul>
 */
@Data
public class EmitterSimulatorRQ {

    @NotNull
    private EmitterType type;

    @NotNull
    private String host;

    @NotNull
    private String name;

    private String topic;

    private String hostId;

    private String password;

    @NotEmpty
    private List<BasePointDTO> pointList;

    private Double speed = 10D; 

    private SpeedUnit speedUnit = SpeedUnit.M_S;

    private Integer cycle = 1;

    @NotNull
    private String format;

    public void setSpeedUnit(String unit){
        this.speedUnit = SpeedUnit.fromUnit(unit);
    }

    public void setSpeedUnit(SpeedUnit unit){
        this.speedUnit = unit;
    }
}
