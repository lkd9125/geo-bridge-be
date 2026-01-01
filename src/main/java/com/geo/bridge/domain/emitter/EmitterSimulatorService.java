package com.geo.bridge.domain.emitter;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.stereotype.Service;

import com.geo.bridge.domain.emitter.integration.EmitterIntegrationService;
import com.geo.bridge.domain.emitter.integration.client.EmitterClient;
import com.geo.bridge.domain.emitter.model.CreateSimulatorClientDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 데이터 시뮬레이션 서비스
 * 
 * <p>기능</p>
 * <ul>
 *     <li>{@link #createSimulatorCoordinates(List, Double, int)} 위치 데이터 계산 후 발행</li>
 *     <li>{@link #createSimulatorClient(CreateSimulatorClientDTO)} 시뮬레이션 클라이언트 생성</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmitterSimulatorService {

    private final EmitterIntegrationService emitterIntegrationService;
    private final GeodeticCalculator calc = new GeodeticCalculator(DefaultGeographicCRS.WGS84); 

    /**
     * coordinates와 속도에 해당되는 좌표 리스트 생성
     * @param routeCoordinates 유저가 선택한 좌표 리스트
     * @param speed 속도(m/s)
     * @param cycle 반복 주기
     * @return
     */
    public Mono<List<Coordinate>> createSimulatorCoordinates(List<Coordinate> routeCoordinates, Double speed, int cycle){
        // 최소 한번은 반복
        if(cycle < 1){
            cycle = 1;
        }

        // 첫 좌표와 마지막 좌표가 같지 않으면 첫좌표 추가
        Coordinate startCoord = routeCoordinates.get(0);
        Coordinate endCoord = routeCoordinates.get(routeCoordinates.size() - 1);
        if(!startCoord.equals(endCoord)){
            routeCoordinates.add(startCoord);
        }
        

        List<Coordinate> timeStepCoordinates = new ArrayList<>();
        
        if (routeCoordinates == null || routeCoordinates.size() < 2) {
            return Mono.just(timeStepCoordinates);
        }

        // 시작점 추가 (0초 지점)
        timeStepCoordinates.add(routeCoordinates.get(0));

        // 현재 1초를 채우기 위해 더 가야할 남은 거리
        double remainingDistForSec = speed;

        // 경로의 각 구간(Segment)을 순회
        for (int i = 0; i < routeCoordinates.size() - 1; i++) {
            Coordinate startNode = routeCoordinates.get(i);
            Coordinate endNode = routeCoordinates.get(i + 1);

            // 현재 구간에서의 시작점 (처음엔 startNode지만, 중간에 점이 찍히면 그곳이 시작점이 됨)
            Coordinate currentPos = startNode;

            while (true) {
                // 1. 현재 위치에서 다음 노드까지의 거리와 방위각 계산
                calc.setStartingGeographicPoint(currentPos.x, currentPos.y);
                calc.setDestinationGeographicPoint(endNode.x, endNode.y);

                double distToNextNode = calc.getOrthodromicDistance(); // 미터 단위 거리
                double azimuth = calc.getAzimuth(); // 방위각

                // 2. 다음 노드까지의 거리가 이번 1초를 채우기에 충분한 경우
                if (distToNextNode >= remainingDistForSec) {
                    // 방위각 방향으로 남은 거리만큼 이동하여 새로운 좌표 생성
                    calc.setStartingGeographicPoint(currentPos.x, currentPos.y);
                    calc.setDirection(azimuth, remainingDistForSec);
                    Point2D destPoint = calc.getDestinationGeographicPoint();
                    
                    Coordinate newPoint = new Coordinate(destPoint.getX(), destPoint.getY());
                    
                    // 결과 리스트에 추가 (1초 경과 지점)
                    timeStepCoordinates.add(newPoint);

                    // 현재 위치를 방금 찍은 점으로 업데이트
                    currentPos = newPoint;
                    
                    // 1초를 채웠으므로, 다음 1초를 위해 거리 리셋
                    remainingDistForSec = speed;
                } 
                // 3. 다음 노드까지 거리가 짧아서, 1초를 다 못 채우고 노드를 지나가는 경우
                else {
                    // 갈 수 있는 만큼만 거리 차감하고, 다음 세그먼트(for문)로 넘어감
                    remainingDistForSec -= distToNextNode;
                    break; // while 문 탈출 -> 다음 i로 이동
                }
            }
        }

        // 반복주기만큼 좌표 갯수추가
        List<Coordinate> result = new ArrayList<>();
        for(int i = 0; i < cycle; i++){
            result.addAll(timeStepCoordinates);
        }
        

        return Mono.just(result);
    }

    /**
     * 시뮬레이션 클라이언트 생성
     * @param dto 클라이언트 생성용 DTO
     * @return 생성 식별 번호
     */
    public Mono<EmitterClient> createSimulatorClient(CreateSimulatorClientDTO dto){
        return emitterIntegrationService.createIntegrationEmitterClient(dto.getName(), dto.getHost(), dto.getTopic(), dto.getUsername(), dto.getPassword(), dto.getType());
    }

}
