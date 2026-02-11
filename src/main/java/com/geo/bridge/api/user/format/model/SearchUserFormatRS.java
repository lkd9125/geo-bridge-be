package com.geo.bridge.api.user.format.model;

import java.time.LocalDateTime;

import com.geo.bridge.domain.user.format.dto.entity.FormatDTO;

import lombok.Data;

/**
 * Emitter Client 저장 RS Web Model
 * 
 * <p>변수</p>
 * <ul>
 *     <li>{@link #name} : 클라이어트 식별 이름</li>
 *     <li>{@link #format} : 데이터 포맷</li>
 *     <li>{@link #contentType} : 컨텐츠 타입</li>
 *     <li>{@link #createDt} : 생성 일자</li>
 *     <li>{@link #updateDt} : 수정 일자</li>
 *     <li>{@link #createAt} : 생성자</li>
 *     <li>{@link #updateAt} : 수정자</li>
 * </ul>
 * 
 * <p>기능</p>
 *  * <ul>
 *     <li>{@link #fromDTO(HostDTO)} : DTO를 SearchEmitterClientInfoRS로 변환</li>
 * </ul>
 */
@Data
public class SearchUserFormatRS {

    private Long idx;
    private String name;
    private String format;
    private String contentType;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;
    private String createAt;
    private String updateAt;

    /**
     * DTO를 SearchEmitterClientInfoRS로 변환
     * @param dto DB Emitter Client Table DTO
     * @return
     */
    public static SearchUserFormatRS fromDTO(FormatDTO dto){
        SearchUserFormatRS rs = new SearchUserFormatRS();
        rs.setIdx(dto.getIdx());
        rs.setName(dto.getName());
        rs.setFormat(dto.getFormat());
        rs.setContentType(dto.getContentType());
        rs.setCreateDt(dto.getCreateDt());
        rs.setCreateAt(dto.getCreateAt());
        rs.setUpdateDt(dto.getUpdateDt());
        rs.setUpdateAt(dto.getUpdateAt());

        return rs;
    }
}
