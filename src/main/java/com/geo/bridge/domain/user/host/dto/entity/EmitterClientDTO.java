package com.geo.bridge.domain.user.host.dto.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.geo.bridge.domain.emitter.integration.model.EmitterType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Emitter DB Table DTO
 * <p>변수</p>
 * <ul>
 *     <li>{@link #idx} : 클라이어트 고유 키 값</li>
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
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("EMITTER_CLIENT")
public class EmitterClientDTO {

    @Id
    @Column("IDX")
    private Long idx;

    @Column("NAME")
    private String name;

    @Column("HOST")
    private String host;

    @Column("TYPE")
    private EmitterType type;

    @Column("TOPIC")
    private String topic;

    @Column("HOST_ID")
    private String hostId;

    @Column("PASSWORD")
    private String password;

    @Column("CREATE_DT")
    private LocalDateTime createDt;

    @Column("UPDATE_DT")
    private LocalDateTime updateDt;

    @Column("CREATE_AT")
    private String createAt;

    @Column("UPDATE_AT")
    private String updateAt;
}
