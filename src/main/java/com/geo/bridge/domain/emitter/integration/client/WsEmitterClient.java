package com.geo.bridge.domain.emitter.integration.client;

import java.net.URI;
import java.util.Map;

import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

/**
 * WebSocket 기반 Emitter Client 구현체.
 *
 * <p>기능</p>
 * <ul>
 *  <li>{@link #connect()} WebSocket 연결 수행</li>
 *  <li>{@link #disconnect()} 세션/리소스 정리</li>
 *  <li>{@link #isConnected()} 연결 상태 확인</li>
 *  <li>{@link #send(String)} 텍스트 메시지 전송</li>
 * </ul>
 *
 * <p>
 * {@link ReactorNettyWebSocketClient}를 사용해 지정된 {@code host} URI로 연결합니다.
 * </p>
 */
@Data
@Slf4j
public class WsEmitterClient implements EmitterClient{

    private String name;
    private String host;
    private Map<String, String> parameter;
    private WebSocketClient client = new ReactorNettyWebSocketClient();
    private WebSocketSession session;
    private Disposable disposable;

    /**
     * WS Emitter Client 생성자.
     *
     * @param name 클라이언트 식별명
     * @param host WebSocket URI (예: {@code "ws://localhost:8080/ws"})
     * @param parameter 추가 파라미터(옵션)
     */
    public WsEmitterClient(String name, String host, Map<String, String> parameter){
        this.name = name;
        this.host = host;
        this.parameter = parameter;

        this.connect();
    }

    /**
     * WebSocket 서버로 연결을 수행하고 세션을 유지합니다.
     */
    @Override
    public void connect() {
        URI uri = URI.create(host);
        disposable = client.execute(uri, session -> {
                this.session = session;
                return session.receive().then();
            })
            .doOnSuccess(monoVoid -> log.info("CONNECTED :: {}", name))
            .onErrorResume(e -> {
                log.error("THIS {} CONNECTED ERROR :: {}", name, e.getMessage());
                return Mono.empty();
            })
            .subscribe();
    }

    /**
     * 세션을 종료하고, 내부 리소스를 해제합니다.
     *
     * <p>
     * WebFlux/WebSocket 특성상 close는 비동기이며, 안전하게 null 처리로 상태를 정리합니다.
     * </p>
     */
    @Override
    public void disconnect() {
        try {
            if (session != null && session.isOpen()) {
                session.close().subscribe();
                this.session = null;
            }

            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
                disposable = null;
            }
        } catch (Exception e) {
            log.error("disconnect error", e);
        }
    }

    @Override
    public Boolean isConnected() {
        return session != null && session.isOpen();
    }

    /**
     * 현재 WebSocket 세션으로 텍스트 메시지를 전송합니다.
     *
     * @param sendData 전송할 payload
     * @return 전송 완료 시그널(연결이 없으면 empty)
     */
    @Override
    public Mono<Void> send(String sendData) {
        if(isConnected()){
            return this.session.send(
                Mono.just(this.session.textMessage(sendData))
            )
            .then();
        } else {
            return Mono.empty();
        }
    }

    @Override
    public String getTopic() {
        return null;
    }

}
