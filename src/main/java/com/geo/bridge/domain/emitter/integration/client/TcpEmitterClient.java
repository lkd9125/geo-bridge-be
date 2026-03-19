package com.geo.bridge.domain.emitter.integration.client;

import java.util.Map;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

/**
 * TCP 기반 Emitter Client 구현체.
 *
 * <p>기능</p>
 * <ul>
 *  <li>{@link #connect()} TCP 연결 수행</li>
 *  <li>{@link #disconnect()} TCP 연결 해제</li>
 *  <li>{@link #isConnected()} 연결 상태 확인</li>
 *  <li>{@link #send(String)} 데이터 전송</li>
 * </ul>
 *
 * <p>
 * 입력으로 받은 {@code host} 문자열에서 호스트/포트를 파싱해 연결을 생성합니다.
 * </p>
 */
@Data
@Slf4j
public class TcpEmitterClient implements EmitterClient{

    private String name;

    private String host;

    private int port;

    private Map<String, String> parameter;

    private TcpClient tcpClient;
    
    private Connection connection;

    /**
     * TCP Emitter Client 생성자.
     *
     * @param name 클라이언트 식별명
     * @param host {@code "host:port"} 형태의 주소 (예: {@code "127.0.0.1:12900"})
     * @param parameter 추가 파라미터(옵션)
     * @throws IllegalArgumentException host 포맷이 {@code host:port}가 아닌 경우
     */
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

    /**
     * 현재 TCP 연결 상태를 확인합니다.
     *
     * @return 연결 객체가 존재하고 dispose 되지 않았으면 {@code true}
     */
    @Override
    public Boolean isConnected() {
        // 전역변수처럼 들고 있는 connection 객체로 상태 체크
        return connection != null && !connection.isDisposed();
    }

    /**
     * 연결된 TCP 소켓으로 문자열 데이터를 전송합니다.
     *
     * @param sendData 전송할 payload
     * @return 전송 완료 시그널(연결이 없으면 empty)
     */
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
