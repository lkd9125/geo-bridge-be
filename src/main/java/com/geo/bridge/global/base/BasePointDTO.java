package com.geo.bridge.global.base;

import org.locationtech.jts.geom.Coordinate;

import lombok.Data;

/**
 * 좌표 모델
 * 
 * <p>변수</p>
 * <ul>
 *  <li>{@link #lat} 위도</li>
 *  <li>{@link #lon} 경도</li>
 *  <li>{@link #heading} 헤딩방향</li>
 * </ul>
 * 
 * <p>기능</p>
 * <ul>
 *  <li>{@link #toCoordinate()} Coordinate 변환</li>
 * </ul> 
 */
@Data
public class BasePointDTO {

    private Double lat;

    private Double lon;

    private Double heading;

    /**
     * Coordinate 변환
     * @return Geometry Coordinate 객체
     */
    public Coordinate toCoordinate(){
        return new Coordinate(this.lon, this.lat);
    }

}
