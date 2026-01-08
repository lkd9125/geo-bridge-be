package com.geo.bridge.domain.user.host;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.geo.bridge.domain.user.host.dto.SearchEmitterDTO;
import com.geo.bridge.domain.user.host.dto.entity.ClientHostDTO;
import com.geo.bridge.domain.user.host.repository.EmitterClientRepository;
import com.geo.bridge.global.base.BasePageRS;
import com.geo.bridge.global.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Client 생성
 * <p>기능</p>
 * <ul>
 *     <li>{@link #createClient()} 클라이언트 생성</li>
 *     <li>{@link #getAllEmitterClient(SearchEmitterDTO, Pageable)} 클라이언트 리스트 조회</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmitterClientService {

    private final EmitterClientRepository emitterClientRepository;


    /**
     * DB Client Infomation Save
     * @param mono 생성객체
     * @return
     */
    public Mono<Void> createClient(Mono<ClientHostDTO> mono){
        return mono
            .doOnNext(rq -> log.info(JsonUtils.toJson(rq)))
            .flatMap(emitterClientRepository::save)
            .then();
    }

    /**
     * DB Client Infomation Search
     * @param searchDTO 검색객체
     * @param pageable 페이지네이션
     * @return
     */
    public Mono<BasePageRS<ClientHostDTO>> getAllEmitterClient(SearchEmitterDTO searchDTO, Pageable pageable){
        return emitterClientRepository.findBySearch(searchDTO, pageable);
    }
    
}
