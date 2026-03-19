package com.geo.bridge.domain.emitter.integration.client;

import java.util.Map;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

@Data
@Slf4j
public class TcpEmitterClient implements EmitterClient{

    private String name;

    private String host;

    private int port;

    private Map<String, String> parameter;

    private TcpClient tcpClient;
    
    private Connection connection;

    public TcpEmitterClient(String name, String host, Map<String, String> parameter){
        String[] url = host.split(":");
        if(url == null || url.length != 2){
            throw new IllegalArgumentException("HOST Formated Error : " + host);
        }
        this.host = url[0];
        this.port = Integer.parseInt(url[1]);

        this.name = name;
        this.connect();
    }
    

    @Override
    public void connect() {
        disconnect();

        this.tcpClient = TcpClient.create().host(host).port(port);
        this.tcpClient
            .connect()
            .subscribe(conn -> {
                this.connection = conn;
                log.info("TCP Connected to {}:{} :: {}", this.host, this.port, this.name);

                this.connection.onDispose().subscribe(
                    null, 
                    error -> log.error("CONNECTION DISPOSE ERROR :: {}", error.getMessage()), 
                    () -> {
                        log.warn("CONNECTION CLOSED :: {}", name);
                        this.connection = null; // 필드 초기화
                    }
                );
            },
            error -> {
                log.error("TCP CONNECTION FAILED :: {} - {}", name, error.getMessage());
                this.connection = null;
            });
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            connection.dispose();
            connection = null;
        }
    }

    @Override
    public Boolean isConnected() {
        // 전역변수처럼 들고 있는 connection 객체로 상태 체크
        return connection != null && !connection.isDisposed();
    }

    @Override
    public Mono<Void> send(String sendData) {
        if (isConnected()) {
            // Sink 없이 직접 Outbound 스트림에 태워서 보냄
            // outbound()는 데이터를 소켓 버퍼로 밀어넣는 입구야
            return this.connection.outbound()
                    .sendString(Mono.just(sendData)) 
                    .then(); 
        } else {
            log.warn("SEND FAILED (NOT CONNECTED) :: {}", name);
            return Mono.empty();
        }
    }

    @Override
    public String getTopic() {
        return null;
    }

}
