package com.geo.bridge.domain.emitter.repository.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.geo.bridge.domain.emitter.dto.SearchEmitterDTO;
import com.geo.bridge.domain.emitter.dto.entity.EmitterClientDTO;
import com.geo.bridge.domain.emitter.repository.EmitterClientBaseRepository;
import com.geo.bridge.global.base.BasePageRS;

import reactor.core.publisher.Mono;

@Repository
public class EmitterClientBaseRepositoryImpl implements EmitterClientBaseRepository{

    @Override
    public Mono<BasePageRS<EmitterClientDTO>> findBySearch(SearchEmitterDTO searchDTO, Pageable pageable) {
        
        return null;
    }

}
