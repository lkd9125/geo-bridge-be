package com.geo.bridge.api.user.format.model;

import com.geo.bridge.domain.user.format.dto.SearchFormatDTO;

import lombok.Data;

@Data
public class SearchUserFormatRQ {

    private Long idx;

    public SearchFormatDTO toDto(){ 
        return SearchFormatDTO.builder()
            .idx(idx)
            .build();
    }

}
