package com.geo.bridge.domain.emitter.context.model;

import lombok.Getter;

@Getter
public enum EmitterClientStatus {

    WAIT, // 대기
    PLAYING, // 활성화
    END, // 종료
    ;
}
