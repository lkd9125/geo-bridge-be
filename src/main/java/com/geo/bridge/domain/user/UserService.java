package com.geo.bridge.domain.user;

import org.springframework.stereotype.Service;

import com.geo.bridge.domain.user.dto.entity.UserDTO;
import com.geo.bridge.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * <p>기능</p>
 * 
 * <ul>
 *  <li>유저 호스트 검색</li>
 *  <li>유저 데이터 포맷 검색</li>
 *  <li>유저 생성</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    
    /**
     * 유저 생성
     * @param userMono 유저 mono
     * @return
     */
    public Mono<Void> createUser(Mono<UserDTO> userMono){
        return userMono.flatMap(userRepository::save)
            .then();
    }

}