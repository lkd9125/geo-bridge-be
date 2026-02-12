package com.geo.bridge.domain.emitter.integration.client;

import java.util.Map;
import java.util.UUID;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;

import com.geo.bridge.global.config.IOScheduler;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


/**
 * MQTT 클라이언트 객체
 * <p>변수</p>
 * <ul>
 *     <li>{@link #name} : 클라이어트 식별 이름</li>
 *     <li>{@link #host} : 연결 호스트</li>
 *     <li>{@link #isConnected} : 연결 여부</li>
*      <li>{@link #topic} : MQTT 토픽</li>
*      <li>{@link #username} : 아이디</li>
*      <li>{@link #password} : 비밀번호</li>
 * </ul>
 */
@Data
@Slf4j
public class MqttEmitterClient implements EmitterClient{

    private String name;
    private String host;
    private boolean isConnected = false;
    private String topic;
    private String username;
    private String password;
    private MqttClient mqttClient;
    private Map<String, String> parameter;
    
    /**
     * MQTT 클라이언트 생성과 동시에 연결
     * @param name
     * @param host
     * @param topic
     * @param username
     * @param password
     */
    public MqttEmitterClient(String name, String host, String topic, String username, String password, Map<String, String> parameter){
        this.name = name;
        this.host = ensureProtocol(host);
        this.topic = topic;
        this.username = username;
        this.password = password;
        this.parameter = parameter;

        this.connect();
    }

    /**
     * host에 프로토콜이 없으면 tcp://를 자동으로 추가
     * @param host 호스트 주소
     * @return 프로토콜이 포함된 호스트 주소
     */
    private String ensureProtocol(String host) {
        if (host == null || host.isBlank()) {
            return host;
        }
        // 이미 프로토콜이 있으면 그대로 반환 (tcp://, ssl://, ws:// 등)
        if (host.contains("tcp://")) {
            return host;
        }
        // 프로토콜이 없으면 tcp:// 추가
        return "tcp://" + host;
    }

    @Override
    public void connect() {
        String clientId = "BRIDGE_CLIENT_%s_%s".formatted(UUID.randomUUID(), this.name);
        try{
            MqttConnectionOptions opts = new MqttConnectionOptions();
            opts.setServerURIs(new String[]{this.host});
            // Username과 Password 설정
            if (this.username != null && !this.username.isBlank()) {
                opts.setUserName(this.username);
            }
            if (this.password != null && !this.password.isBlank()) {
                opts.setPassword(this.password.getBytes());
            }

            MqttClient mqttClient = new MqttClient(this.host, clientId, new MemoryPersistence());
            mqttClient.connect(opts);
            
            this.mqttClient = mqttClient;
            log.info("Try '{}' TCP Connect {} : {}", name, clientId, this.host);
            this.isConnected = true;
        } catch (MqttException e){
            log.error("THIS {} CONNECTED ERROR :: {}", this.name, e.getMessage());
        } catch (Exception e){
            log.error("OTHER ERROR :: {}", e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        if(this.isConnected()){
            try {
                this.mqttClient.disconnect();
            } catch (MqttException e) {
                log.warn("MQTT disconnect error: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public Boolean isConnected() {
        return this.isConnected;
    }

    @Override
    public Mono<Void> send(String sendData) {
        return Mono.<Void>fromRunnable(() -> {
                try {
                    mqttClient.publish(
                        topic,
                        sendData.getBytes(),
                        1,
                        false
                    );
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
            })
            .subscribeOn(IOScheduler.scheduler());
    }

}
