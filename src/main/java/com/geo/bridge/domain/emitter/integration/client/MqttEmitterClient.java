package com.geo.bridge.domain.emitter.integration.client;

import java.util.Arrays;
import java.util.UUID;

import org.eclipse.paho.mqttv5.client.IMqttAsyncClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.integration.mqtt.core.Mqttv5ClientManager;

import com.geo.bridge.global.config.IOScheduler;
import com.geo.bridge.global.utils.JsonUtil;

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
    private IMqttAsyncClient mqttClient;
    
    /**
     * MQTT 클라이언트 생성과 동시에 연결
     * @param name
     * @param host
     * @param topic
     * @param username
     * @param password
     */
    public MqttEmitterClient(String name, String host, String topic, String username, String password){
        this.name = name;
        this.host = host;
        this.topic = topic;
        this.username = username;
        this.password = password;

        this.connect();
    }

    /**
     * MQTT 연결 기능
     */
    @Override
    public void connect() {
        String clientId = "BRIDGE_CLIENT_%s_%s".formatted(UUID.randomUUID(), this.name);
        try{
            MqttConnectionOptions options = new MqttConnectionOptions();
            options.setServerURIs(new String[]{this.host});
    
            Mqttv5ClientManager manager = new Mqttv5ClientManager(options, clientId);

            this.mqttClient = manager.getClient();
            this.mqttClient.connect();
            log.info("Try '{}' TCP Connect {} : {}", name, clientId, this.host);
            this.isConnected = true;
        } catch (MqttException e){
            log.error("THIS {} CONNECTED ERROR :: {}", this.name, e.getMessage());
        } catch (Exception e){
            log.error("OTHER ERROR :: {}", e.getMessage());
        }
    }

    /**
     * MQTT 연결 해제 기능
     */
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

    /**
     * 연결여부 조회
     */
    @Override
    public Boolean isConnected() {
        return this.isConnected;
    }

    /**
     * 데이터 전송 [x,y] 형태
     */
    @Override
    public Mono<Void> send(Coordinate coordinate) {
        // [X,Y] 형태로 전송
        return Mono.<Void>fromRunnable(() -> {
                try {
                    mqttClient.publish(
                        topic,
                        JsonUtil.toJson(Arrays.asList(coordinate.x, coordinate.y)).getBytes(),
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
