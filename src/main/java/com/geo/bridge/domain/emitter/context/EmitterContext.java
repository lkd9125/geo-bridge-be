package com.geo.bridge.domain.emitter.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.locationtech.jts.geom.Coordinate;

import com.geo.bridge.domain.emitter.context.model.EmitterClientManager;
import com.geo.bridge.domain.emitter.context.model.EmitterClientStatus;
import com.geo.bridge.domain.emitter.integration.client.EmitterClient;
import com.geo.bridge.global.base.BasePointDTO;
import com.geo.bridge.global.exception.BaseException;
import com.geo.bridge.global.exception.ExceptionCode;

import lombok.extern.slf4j.Slf4j;

/**
 * Emitter Client Conext
 * Emitter Client를 등록, 실행, 상태 체크 객체
 * 
 * <p>기능</p>
 * <ul>
 *  <li>{@link #put(String, EmitterClient, String, List)} EmitterClient를 Context에 등록</li>
 *  <li>{@link #excute(String, String)} Emitter 실행</li>
 * </ul>
 */
@Slf4j
public class EmitterContext {

    // MAP<CustNo(유저식별자), MAP<UUID, EmitterClientManager>>
    private static Map<String, Map<String, EmitterClientManager>> emitterManagerMap = new ConcurrentHashMap<>();

    /**
     * EmitterClient를 Context에 등록
     * @param custNo 유저번호
     * @param client Emitter Client
     * @param format 전송 포맷팅
     * @param cooridates 전송 좌표 데이터
     * @param cycle 반복 횟수
     * @return UUID
     */
    public static String put(String custNo, EmitterClient client, String format, List<BasePointDTO> cooridates, int cycle){
        if(cycle < 1){
            cycle = 1;
        }

        EmitterClientManager manager = new EmitterClientManager();
        manager.setClient(client);
        manager.setFormat(format);
        manager.setCooridnates(cooridates);
        manager.setCycle(cycle);
        manager.setBaseParameters(client.getParameter());

        Map<String, EmitterClientManager> clientMap = emitterManagerMap.get(custNo);
        if(clientMap == null){
            clientMap = new HashMap<>();
        }

        clientMap.put(manager.getUuid(), manager);
        emitterManagerMap.put(custNo, clientMap);
        
        return manager.getUuid();
    }

    /**
     * Emitter 실행
     * @param custNo 유저번호
     * @param uuid Manager 고유키
     * @return Manager 고유키
     */
    public static String excute(String custNo, String uuid){
        Map<String, EmitterClientManager> clientMap = emitterManagerMap.get(custNo);
        if(clientMap == null){
            throw new BaseException(ExceptionCode.PARAMETER_INVALID);
        }

        EmitterClientManager emitterManager = clientMap.get(uuid);
        if(emitterManager != null && emitterManager.getStatus() != EmitterClientStatus.PLAYING){
            emitterManager.excute(SseEmiterContext.getSseEmiter(custNo));
        }

        return uuid;
    }

    /**
     * Emitter Manager 삭제
     * @param custNo
     * @param uuid
     */
    public static void remove(String custNo, String uuid){
        Map<String, EmitterClientManager> clientMap = emitterManagerMap.get(custNo);
        if(clientMap == null){
            throw new BaseException(ExceptionCode.PARAMETER_INVALID);
        }

        EmitterClientManager delManager = clientMap.remove(uuid);
        if(delManager != null){
            log.info("MANAGER '{}' IS REMOVE", delManager.getUuid());   
        } else {
            log.info("ALREADY REMOVE '{}'", uuid);
        }
    }

    /**
     * Emitter 정지
     * @param custNo
     * @param uuid
     */
    public static void stop(String custNo, String uuid){
        Map<String, EmitterClientManager> clientMap = emitterManagerMap.get(custNo);
        if(clientMap == null){
            throw new BaseException(ExceptionCode.PARAMETER_INVALID);
        }

        EmitterClientManager emitterManager = clientMap.get(uuid);
        emitterManager.stop();
    }
}
