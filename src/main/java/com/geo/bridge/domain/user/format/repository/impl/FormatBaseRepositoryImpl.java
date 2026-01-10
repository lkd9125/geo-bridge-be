package com.geo.bridge.domain.user.format.repository.impl;

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FormatBaseRepositoryImpl {

    private final R2dbcEntityTemplate template;
    private final String TABLE = "FORMAT";

    

}
