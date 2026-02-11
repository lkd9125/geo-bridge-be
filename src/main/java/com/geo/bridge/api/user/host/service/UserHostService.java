package com.geo.bridge.api.user.host.service;

import org.springframework.stereotype.Service;

import com.geo.bridge.api.user.host.model.CreateUserHostRQ;
import com.geo.bridge.api.user.host.model.SearchUserHostRQ;
import com.geo.bridge.api.user.host.model.SearchUserHostRS;
import com.geo.bridge.domain.user.UserService;
import com.geo.bridge.domain.user.host.HostService;
import com.geo.bridge.domain.user.host.dto.SearchHostDTO;
import com.geo.bridge.global.base.BasePageRQ;
import com.geo.bridge.global.base.BasePageRS;
import com.geo.bridge.global.security.SecurityHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * DB 저장소에 클라이언트 정보 생성 서비스 로직
 * Domain 접근
 * 
 * <p>기능</p>
 * <ul>
 *     <li>{@link #createEmitterClientInfo(Mono)} 클라이언트 정보 생성</li>
 *     <li>{@link #getAllEmitterClientInfo(SearchUserHostRQ, BasePageRQ)} 클라이언트 정보 생성</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserHostService {

    private final HostService clientHostService;
    private final UserService userService;

    /**
     * Emitter Client 데이터 생성
     * 1. Emitter Client Infomation DTO 변환
     * 2. DB Save 호출
     * @param mono Web RQ Model
     * @return
     */
    public Mono<Void> createClientHost(Mono<CreateUserHostRQ> mono){
        return mono
            .flatMap(CreateUserHostRQ::toDto)
            .flatMap(dto -> clientHostService.createClientHost(Mono.just(dto)));
    }

    /**
     * Emitter Client 정보 리스트 조회 (페이징)
     * 1. Emitter Client DTO 객체 변환
     * 2. DB 데이터 호출
     * 3. Web RS 객체 변환
     * @param rq 검색 RQ
     * @param page 페이지네이션
     * @return
     */
    public Mono<BasePageRS<SearchUserHostRS>> getAllEmitterClientInfo(SearchUserHostRQ rq, BasePageRQ page){
        SearchHostDTO searchDTO = rq.toDto();

        return SecurityHelper.securityHolder()
            .flatMap(user -> userService.getUserByUsername(user.getUsername()))
            .flatMap(user -> {
                searchDTO.setUserId(user.getUsername());
                return clientHostService.getAllClientHost(searchDTO, page);
            })
            .flatMap(pageRS ->
                Flux.fromIterable(pageRS.getList())
                    .map(SearchUserHostRS::fromDTO)
                    .collectList()
                    .map(newList -> 
                        BasePageRS.of(
                            newList, 
                            pageRS.getPage(), 
                            pageRS.getSize(), 
                            pageRS.getRange(), 
                            pageRS.getTotalCount()
                        )
                    )
            );
    }

}
