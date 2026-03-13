package com.geo.bridge.domain.emitter.context.model;

import lombok.Getter;

@Getter
public enum EmitterClientStatus {

    WAIT, // 대기
    PLAYING, // 활성화
    CONNECTED_FAIL, // 연결실패
    END, // 종료
    ;
}
