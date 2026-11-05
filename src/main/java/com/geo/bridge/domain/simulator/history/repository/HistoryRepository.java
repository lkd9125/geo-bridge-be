package com.geo.bridge.domain.simulator.history.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.geo.bridge.domain.simulator.history.dto.entity.History;

public interface HistoryRepository extends R2dbcRepository<History, Long>, HistoryBaseRepository {
    
}
