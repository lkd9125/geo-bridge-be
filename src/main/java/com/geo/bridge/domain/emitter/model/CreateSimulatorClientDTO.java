package com.geo.bridge.domain.emitter.model;

import java.util.Map;

import com.geo.bridge.domain.emitter.integration.model.EmitterType;

import lombok.Data;

/**
 * 시뮬레이션 클라이언트 생성 객체
 * 
 * <p>변수</p>
 * <ul>
 *     <li>{@link #name} : 클라이어트 식별 이름</li>
 *     <li>{@link #host} : 연결 호스트</li>
 *      <li>{@link #topic} : (옵션) MQTT 토픽</li>
 *      <li>{@link #username} : (옵션) 아이디</li>
 *      <li>{@link #password} : (옵션) 비밀번호</li>
 *      <li>{@link #type} : 클라이언트 타입</li>
 *      <li>{@link #parameter} : 추가 파라미터(포맷팅)</li>
 * </ul>
 */
@Data
public class CreateSimulatorClientDTO {

    private String name;
    private String host;
    private String topic;
    private String username;
    private String password;
    private EmitterType type;
    private Map<String, String> parameter;

}