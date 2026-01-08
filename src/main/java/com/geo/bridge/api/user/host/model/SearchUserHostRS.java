package com.geo.bridge.api.user.host.model;

import java.time.LocalDateTime;

import com.geo.bridge.domain.emitter.integration.model.EmitterType;
import com.geo.bridge.domain.user.host.dto.entity.ClientHostDTO;

import lombok.Data;

/**
 * Emitter Client 저장 RS Web Model
 * 
 * <p>변수</p>
 * <ul>
 *     <li>{@link #name} : 클라이어트 식별 이름</li>
 *     <li>{@link #host} : 연결 호스트</li>
 *     <li>{@link #type} : 연결 타입</li>
 *     <li>{@link #topic} : 토픽</li>
 *     <li>{@link #hostId} : 아이디</li>
 *     <li>{@link #password} : 비밀번호</li>
 *     <li>{@link #createDt} : 생성 일자</li>
 *     <li>{@link #updateDt} : 수정 일자</li>
 *     <li>{@link #createAt} : 생성자</li>
 *     <li>{@link #updateAt} : 수정자</li>
 * </ul>
 * 
 * <p>기능</p>
 *  * <ul>
 *     <li>{@link #fromDTO(ClientHostDTO)} : DTO를 SearchEmitterClientInfoRS로 변환</li>
 * </ul>
 */
@Data
public class SearchUserHostRS {

    private Long idx;

    private String name;

    private String host;

    private EmitterType type;

    private String topic;

    private String hostId;

    private String password; 

    private LocalDateTime createDt;

    private String createAt;

    private LocalDateTime updateDt;

    private String updateAt;

    /**
     * DTO를 SearchEmitterClientInfoRS로 변환
     * @param dto DB Emitter Client Table DTO
     * @return
     */
    public static SearchUserHostRS fromDTO(ClientHostDTO dto){
        SearchUserHostRS rs = new SearchUserHostRS();
        rs.setIdx(dto.getIdx());
        rs.setName(dto.getName());
        rs.setHost(dto.getHost());
        rs.setType(dto.getType());
        rs.setTopic(dto.getTopic());
        rs.setHostId(dto.getHostId());
        rs.setPassword(dto.getPassword());
        rs.setCreateDt(dto.getCreateDt());
        rs.setCreateAt(dto.getCreateAt());
        rs.setUpdateDt(dto.getUpdateDt());
        rs.setUpdateAt(dto.getUpdateAt());

        return rs;
    }

}
