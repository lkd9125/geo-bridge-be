package com.geo.bridge.api.user.format.service;

import org.springframework.stereotype.Service;

import com.geo.bridge.api.user.format.model.CreateFormatRQ;
import com.geo.bridge.domain.user.format.FormatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserFormatService {

    private final FormatService formatService;

    public Mono<Void> createForamt(Mono<CreateFormatRQ> mono){
        return mono
            .map(CreateFormatRQ::toDto)
            .flatMap(dto -> formatService.createForamt(Mono.just(dto)));
    }

}
