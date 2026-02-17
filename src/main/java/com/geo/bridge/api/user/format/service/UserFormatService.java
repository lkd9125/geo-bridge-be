package com.geo.bridge.api.user.format.service;

import org.springframework.stereotype.Service;

import com.geo.bridge.api.user.format.model.CreateFormatRQ;
import com.geo.bridge.api.user.format.model.SearchUserFormatRQ;
import com.geo.bridge.api.user.format.model.SearchUserFormatRS;
import com.geo.bridge.domain.user.UserService;
import com.geo.bridge.domain.user.format.FormatService;
import com.geo.bridge.domain.user.format.dto.SearchFormatDTO;
import com.geo.bridge.global.base.BasePageRQ;
import com.geo.bridge.global.base.BasePageRS;
import com.geo.bridge.global.security.SecurityHelper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserFormatService {

    private final FormatService formatService;
    private final UserService userService;

    /**
     * User Format 저장
     * @param mono FormatTable 데이터
     * @return
     */
    public Mono<Void> createForamt(Mono<CreateFormatRQ> mono){
        return mono
            .flatMap(CreateFormatRQ::toDto)
            .flatMap(dto -> formatService.createForamt(Mono.just(dto)));
    }

    /**
     * User Format 정보 리스트 조회(페이징)
     * @param rq
     * @param page
     * @return
     */
    public Mono<BasePageRS<SearchUserFormatRS>> getAllUserFormat(SearchUserFormatRQ rq, BasePageRQ page) {
        SearchFormatDTO searchDto = rq.toDto();
        return SecurityHelper.securityHolder()
            .flatMap(user -> userService.getUserByUsername(user.getUsername()))
            .flatMap(user -> {
                searchDto.setUserId(user.getUsername());
                return formatService.getAllFormat(searchDto, page);
            })
            .flatMap(pageRS ->
                Flux.fromIterable(pageRS.getList())
                    .map(SearchUserFormatRS::fromDTO)
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

    /**
     * User Format 삭제
     * @param idx Format PK
     * @return
     */
    public Mono<Void> deleteUserFormat(Long idx){
        return formatService.deleteFormat(idx);
    }
    
}
