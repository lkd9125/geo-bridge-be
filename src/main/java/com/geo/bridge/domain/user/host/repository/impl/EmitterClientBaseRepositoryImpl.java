package com.geo.bridge.domain.user.host.repository.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.geo.bridge.domain.user.host.dto.SearchEmitterDTO;
import com.geo.bridge.domain.user.host.dto.entity.ClientHostDTO;
import com.geo.bridge.domain.user.host.repository.EmitterClientBaseRepository;
import com.geo.bridge.global.base.BasePageRS;

import reactor.core.publisher.Mono;

@Repository
public class EmitterClientBaseRepositoryImpl implements EmitterClientBaseRepository{

    @Override
    public Mono<BasePageRS<ClientHostDTO>> findBySearch(SearchEmitterDTO searchDTO, Pageable pageable) {
        
        return null;
    }

}
