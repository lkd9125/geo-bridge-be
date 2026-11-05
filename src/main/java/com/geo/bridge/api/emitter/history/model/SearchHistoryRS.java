package com.geo.bridge.api.emitter.history.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.geo.bridge.domain.simulator.history.dto.entity.History;
import com.geo.bridge.domain.simulator.integration.model.EmitterType;
import com.geo.bridge.global.base.BasePointDTO;
import com.geo.bridge.global.utils.JsonUtils;
import com.geo.bridge.global.utils.SpeedUtils.SpeedUnit;

import lombok.Data;

/**
 * 실행 이력 조회 응답 모델
 */
@Data
public class SearchHistoryRS {

    private Long sn;
    private String title;
    private String description;
    private Boolean favorite;
    private LocalDateTime favoriteDt;
    private LocalDateTime createDt;
    private List<BasePointDTO> path;
    private EmitterType type;
    private String host;
    private String topic;
    private String hostId;
    private String password;
    private Double speed;
    private SpeedUnit speedUnit;
    private Integer cycle;
    private String format;
    private Map<String, String> parameter;

    /**
     * 실행 이력 엔티티를 조회 응답으로 변환합니다.
     * - 경로와 추가 파라미터 JSON을 응답 객체로 변환합니다.
     *
     * @param history - 실행 이력 엔티티
     * @return 실행 이력 조회 응답
     */
    public static SearchHistoryRS fromDTO(History history) {
        SearchHistoryRS rs = new SearchHistoryRS();
        rs.setSn(history.getSn());
        rs.setTitle(history.getTitle());
        rs.setDescription(history.getDescription());
        rs.setFavorite(history.getFavorite());
        rs.setFavoriteDt(history.getFavoriteDt());
        rs.setCreateDt(history.getCreateDt());
        rs.setPath(JsonUtils.fromJson(history.getPath(), new TypeReference<List<BasePointDTO>>() {}));
        rs.setType(history.getType() == null ? null : EmitterType.valueOf(history.getType()));
        rs.setHost(history.getHost());
        rs.setTopic(history.getTopic());
        rs.setHostId(history.getHostId());
        rs.setPassword(history.getHostPassword());
        rs.setSpeed(history.getSpeed());
        rs.setSpeedUnit(history.getSpeedUnit() == null ? null : SpeedUnit.valueOf(history.getSpeedUnit()));
        rs.setCycle(history.getCycle());
        rs.setFormat(history.getFormat());
        rs.setParameter(JsonUtils.fromJson(history.getParameter(), new TypeReference<Map<String, String>>() {}));

        return rs;
    }

}
