package com.geo.bridge.global.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 속도유틸
 * 
 * <p>기능</p>
 * <ul>
 *  <li>{@link #convertToMs(Double, SpeedUnit)} 속도를 M/S 단위로 변환</li>
 * </ul>
 */
public class SpeedUtils {

    /**
     * 속도를 M/S 단위로 변환
     * @param speedValue 속도 수치
     * @param unit 속도 단위
     * @return 변환 된 M/S 속도 수치 값
     */
    public static Double convertToMs(Double speedValue, SpeedUnit unit){
        return switch(unit) {
            case KM_H -> speedValue * 1000.0;
            case M_S -> speedValue;
            default -> speedValue;
        };
    }

    @Getter
    @RequiredArgsConstructor
    public enum SpeedUnit{

        M_S("m/s"),
        KM_H("km/h")
        ;

        private final String unit;

        /**
         * UNIT String을 SpeedUnit Type으로 변환
         * @param unit UNIT String을
         * @return SpeedUnit 상수 값
         */
        public static SpeedUnit fromUnit(String unit){
            for(SpeedUnit speedUnit : SpeedUnit.values()){
                if(speedUnit.getUnit().equals(unit)){
                    return speedUnit;
                }
            }

            return SpeedUnit.M_S;
        }
    }

}
