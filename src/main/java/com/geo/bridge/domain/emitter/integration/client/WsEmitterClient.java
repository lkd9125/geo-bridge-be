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

@Data
@Slf4j
public class WsEmitterClient implements EmitterClient{

    private String name;
    private String host;
    private Map<String, String> parameter;
    private WebSocketClient client = new ReactorNettyWebSocketClient();
    private WebSocketSession session;
    private Disposable disposable;

    public WsEmitterClient(String name, String host, Map<String, String> parameter){
        this.name = name;
        this.host = host;
        this.parameter = parameter;

        this.connect();
    }

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
