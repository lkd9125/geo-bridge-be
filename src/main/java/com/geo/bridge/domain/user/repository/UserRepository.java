package com.geo.bridge.domain.user.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.geo.bridge.domain.user.dto.entity.UserDTO;

import reactor.core.publisher.Mono;

public interface UserRepository extends R2dbcRepository<UserDTO, String>, UserBaseRepository{

}
