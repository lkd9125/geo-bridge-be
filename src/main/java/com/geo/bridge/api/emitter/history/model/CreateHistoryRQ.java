package com.geo.bridge.api.emitter.history.model;

import java.util.List;
import java.util.Map;

import com.geo.bridge.domain.simulator.history.dto.entity.History;
import com.geo.bridge.domain.simulator.integration.model.EmitterType;
import com.geo.bridge.global.base.BasePointDTO;
import com.geo.bridge.global.security.SecurityHelper;
import com.geo.bridge.global.utils.JsonUtils;
import com.geo.bridge.global.utils.SpeedUtils.SpeedUnit;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import reactor.core.publisher.Mono;

/**
 * 실행 이력 저장 요청 모델
 */
@Data
public class CreateHistoryRQ {

    private String title;

    private String description;

    @NotEmpty
    private List<BasePointDTO> path;

    @NotNull
    private EmitterType type;

    @NotNull
    private String host;

    private String topic;

    private String hostId;

    private String password;

    private Double speed = 10D;

    private SpeedUnit speedUnit = SpeedUnit.M_S;

    private Integer cycle = 1;

    @NotNull
    private String format;

    private Map<String, String> parameter;

    /**
     * 실행 이력 엔티티로 변환합니다.
     * - 인증 사용자 PK를 실행 이력에 설정합니다.
     * - 경로와 추가 파라미터를 JSON 문자열로 변환합니다.
     *
     * @return 실행 이력 엔티티
     */
    public Mono<History> toDto() {
        return SecurityHelper.securityHolder()
            .map(user -> History.builder()
                .username(user.getUsername())
                .title(title)
                .description(description)
                .path(JsonUtils.toJson(path))
                .type(type.name())
                .host(host)
                .topic(topic)
                .hostId(hostId)
                .hostPassword(password)
                .speed(speed)
                .speedUnit(speedUnit.name())
                .cycle(cycle)
                .format(format)
                .parameter(JsonUtils.toJson(parameter))
                .build()
            );
    }

}
