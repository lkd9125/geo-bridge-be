package com.geo.bridge.domain.emitter.context;

import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.eclipse.paho.mqttv5.client.MqttClient;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.geo.bridge.domain.emitter.integration.client.EmitterClient;
import com.geo.bridge.domain.emitter.integration.client.MqttEmitterClient;
import com.geo.bridge.domain.emitter.integration.client.WsEmitterClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;

/**
 * 외부 전송 커넥션 공유 컨텍스트.
 *
 * <p>기능</p>
 * <ul>
 *  <li>프로토콜별 공유 커넥션 저장/조회</li>
 *  <li>owner 단위 acquire/release 및 참조 카운트 관리</li>
 *  <li>커넥션 생명주기 이벤트 옵저버 알림</li>
 * </ul>
 */
@Slf4j
public class ConnectionContext {

    private static final Map<String, SharedWsConnection> wsMap = new ConcurrentHashMap<>();
    private static final Map<String, SharedMqttConnection> mqttMap = new ConcurrentHashMap<>();
    private static final CopyOnWriteArrayList<ConnectionObserver> observers = new CopyOnWriteArrayList<>();
 
    public static WebSocketSession getWsClient(String key){
        SharedWsConnection shared = wsMap.get(key);
        return shared == null ? null : shared.session;
    }

    /**
     * WS 커넥션 사용자를 등록하고 참조 카운트를 증가시킵니다.
     *
     * @param key 커넥션 키(예: ws|host|port|path|query)
     * @param ownerId 커넥션 사용자 식별자(UUID 권장)
     */
    public static void acquireWsClient(String key, String ownerId) {
        if (key == null || ownerId == null) {
            throw new IllegalArgumentException("key, ownerId must not be null");
        }

        final boolean[] created = new boolean[] { false };
        SharedWsConnection shared = wsMap.compute(key, (k, existing) -> {
            if (existing == null) {
                created[0] = true;
                existing = new SharedWsConnection();
            }
            existing.owners.add(ownerId);
            existing.refCount.incrementAndGet();
            return existing;
        });

        notifyAcquire("WS", key, ownerId, shared.refCount.get(), created[0]);
    }

    /**
     * WS 커넥션에 실제 세션/정리 리소스를 바인딩합니다.
     *
     * @param key 커넥션 키
     * @param session 연결된 세션
     * @param disposable 연결 유지 구독 핸들
     */
    public static void bindWsClient(String key, WebSocketSession session, Disposable disposable) {
        if (key == null) {
            return;
        }

        wsMap.computeIfPresent(key, (k, shared) -> {
            shared.session = session;
            shared.disposable = disposable;
            return shared;
        });
    }

    /**
     * WS 커넥션 사용을 해제합니다.
     *
     * <p>
     * 마지막 사용자(refCount=0)면 세션 close 및 연결 유지 구독을 dispose한 뒤 Context에서 제거합니다.
     * </p>
     *
     * @param key 커넥션 키
     * @param ownerId 커넥션 사용자 식별자(UUID 권장)
     */
    public static void releaseWsClient(String key, String ownerId) {
        if (key == null || ownerId == null) {
            return;
        }

        final boolean[] released = new boolean[] { false };
        final int[] refCount = new int[] { -1 };

        wsMap.computeIfPresent(key, (k, shared) -> {
            shared.owners.remove(ownerId);
            int next = shared.refCount.decrementAndGet();
            refCount[0] = next;

            if (next <= 0) {
                try {
                    if (shared.session != null && shared.session.isOpen()) {
                        shared.session.close().subscribe();
                    }
                    if (shared.disposable != null && !shared.disposable.isDisposed()) {
                        shared.disposable.dispose();
                    }
                } catch (Exception e) {
                    log.warn("WS release cleanup error: {}", e.getMessage());
                } finally {
                    released[0] = true;
                }
                return null;
            }
            return shared;
        });

        if (refCount[0] >= 0) {
            notifyRelease("WS", key, ownerId, refCount[0], released[0]);
        }
    }

    /**
     * MQTT 커넥션을 owner 기준으로 획득합니다.
     *
     * <p>
     * 동일 key가 이미 존재하면 기존 커넥션을 재사용하고, 없으면 {@code supplier}로 생성합니다.
     * 획득 시 refCount가 증가하며 owner 집합에 등록됩니다.
     * </p>
     *
     * @param key 커넥션 키(예: host|topic|id|credentialHash)
     * @param ownerId 커넥션 사용자 식별자(UUID 권장)
     * @param supplier 신규 커넥션 생성 공급자
     * @return 공유 MQTT 클라이언트
     */
    public static MqttClient acquireMqttClient(String key, String ownerId, Supplier<MqttClient> supplier){
        if (key == null || ownerId == null || supplier == null) {
            throw new IllegalArgumentException("key, ownerId, supplier must not be null");
        }

        final boolean[] created = new boolean[] { false };
        SharedMqttConnection shared = mqttMap.compute(key, (k, existing) -> {
            if (existing == null) {
                created[0] = true;
                existing = new SharedMqttConnection(supplier.get());
            }
            existing.owners.add(ownerId);
            existing.refCount.incrementAndGet();
            return existing;
        });

        notifyAcquire("MQTT", key, ownerId, shared.refCount.get(), created[0]);
        return shared.client;
    }

    /**
     * MQTT 커넥션 사용을 해제합니다.
     *
     * <p>
     * owner를 제거하고 refCount를 감소시킨 뒤, 마지막 사용자(refCount=0)면 disconnect 후
     * Context에서 제거합니다.
     * </p>
     *
     * @param key 커넥션 키
     * @param ownerId 커넥션 사용자 식별자(UUID 권장)
     * @param disconnectAction 마지막 사용자일 때 수행할 정리 로직
     */
    public static void releaseMqttClient(String key, String ownerId, Consumer<MqttClient> disconnectAction){
        if (key == null || ownerId == null) {
            return;
        }

        final boolean[] released = new boolean[] { false };
        final int[] refCount = new int[] { -1 };

        mqttMap.computeIfPresent(key, (k, shared) -> {
            shared.owners.remove(ownerId);
            int next = shared.refCount.decrementAndGet();
            refCount[0] = next;

            if (next <= 0) {
                try {
                    if (disconnectAction != null) {
                        disconnectAction.accept(shared.client);
                    }
                } finally {
                    released[0] = true;
                }
                return null;
            }
            return shared;
        });

        if (refCount[0] >= 0) {
            notifyRelease("MQTT", key, ownerId, refCount[0], released[0]);
        }
    }

    public static MqttClient getMqttClient(String key){
        SharedMqttConnection shared = mqttMap.get(key);
        return shared == null ? null : shared.client;
    }

    public static void setWsClient(String key, WebSocketSession session){
        if(key != null && session != null){
            SharedWsConnection shared = wsMap.computeIfAbsent(key, k -> new SharedWsConnection());
            shared.session = session;
        }
    }

    public static void setMqttClient(String key, MqttClient mqttClient){
        if(key != null && mqttClient != null){
            mqttMap.put(key, new SharedMqttConnection(mqttClient));
        }
    }

    /**
     * 커넥션 생명주기 옵저버 등록.
     * @param observer 옵저버
     */
    public static void addObserver(ConnectionObserver observer) {
        if (observer != null) {
            observers.addIfAbsent(observer);
        }
    }

    /**
     * 커넥션 생명주기 옵저버 제거.
     * @param observer 옵저버
     */
    public static void removeObserver(ConnectionObserver observer) {
        observers.remove(observer);
    }

    /**
     * EmitterClient 타입에 맞는 공유 커넥션 종료 처리를 수행합니다.
     *
     * <p>
     * 공유 커넥션을 사용하는 WS/MQTT는 Context의 release 메서드를 통해 참조 카운트를 정리하고,
     * 그 외 프로토콜은 클라이언트 자체 disconnect 로직에 위임합니다.
     * </p>
     *
     * @param client 종료할 emitter client
     */
    public static void releaseClient(EmitterClient client) {
        if (client == null) {
            return;
        }

        if (client instanceof WsEmitterClient wsClient) {
            releaseWsClient(wsClient.getConnectionKey(), wsClient.getOwnerId());
            wsClient.setSession(null);
            wsClient.setDisposable(null);
            return;
        }

        if (client instanceof MqttEmitterClient mqttClient) {
            releaseMqttClient(mqttClient.getConnectionKey(), mqttClient.getOwnerId(), sharedClient -> {
                try {
                    if (sharedClient != null && sharedClient.isConnected()) {
                        sharedClient.disconnect();
                    }
                } catch (Exception e) {
                    log.warn("MQTT disconnect error: {}", e.getMessage(), e);
                }
            });
            return;
        }

        client.disconnect();
    }

    private static void notifyAcquire(String protocol, String key, String ownerId, int refCount, boolean created) {
        int wsEntries = wsMap.size();
        int mqttEntries = mqttMap.size();
        log.info(
            "[CONNECTION][ACQUIRE] protocol={} key={} owner={} refCount={} created={} wsEntries={} mqttEntries={}",
            protocol, key, ownerId, refCount, created, wsEntries, mqttEntries
        );
        for (ConnectionObserver observer : observers) {
            try {
                observer.onAcquire(protocol, key, ownerId, refCount, created);
            } catch (Exception e) {
                log.warn("ConnectionObserver acquire callback error: {}", e.getMessage());
            }
        }
    }

    private static void notifyRelease(String protocol, String key, String ownerId, int refCount, boolean closed) {
        int wsEntries = wsMap.size();
        int mqttEntries = mqttMap.size();
        log.info(
            "[CONNECTION][RELEASE] protocol={} key={} owner={} refCount={} removed={} wsEntries={} mqttEntries={}",
            protocol, key, ownerId, refCount, closed, wsEntries, mqttEntries
        );
        for (ConnectionObserver observer : observers) {
            try {
                observer.onRelease(protocol, key, ownerId, refCount, closed);
            } catch (Exception e) {
                log.warn("ConnectionObserver release callback error: {}", e.getMessage());
            }
        }
    }

    private static class SharedMqttConnection {
        private final MqttClient client;
        private final AtomicInteger refCount = new AtomicInteger(0);
        private final Set<String> owners = ConcurrentHashMap.newKeySet();

        private SharedMqttConnection(MqttClient client) {
            this.client = client;
        }
    }

    private static class SharedWsConnection {
        private volatile WebSocketSession session;
        private volatile Disposable disposable;
        private final AtomicInteger refCount = new AtomicInteger(0);
        private final Set<String> owners = ConcurrentHashMap.newKeySet();
    }

    /**
     * 커넥션 획득/반납 이벤트를 수신하기 위한 옵저버.
     */
    public interface ConnectionObserver {
        default void onAcquire(String protocol, String key, String ownerId, int refCount, boolean created) {}
        default void onRelease(String protocol, String key, String ownerId, int refCount, boolean closed) {}
    }

}
