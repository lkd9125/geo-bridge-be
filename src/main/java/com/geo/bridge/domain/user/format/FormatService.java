package com.geo.bridge.domain.user.format;

import org.springframework.stereotype.Service;

import com.geo.bridge.domain.user.format.dto.entity.FormatDTO;
import com.geo.bridge.domain.user.format.repository.FormatRepository;
import com.geo.bridge.global.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * HOST 서비스
 * <p>기능</p>
 * <ul>
 *  <li>{@link #createForamt(Mono)} 클라이언트 생성</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FormatService {

    private final FormatRepository formatRepository;

    /**
     * DB FORMAT  데이터 Save
     * @param mono 생성객체
     * @return
     */
    public Mono<Void> createForamt(Mono<FormatDTO> mono){
        return mono
            .doOnNext(dto -> log.info(JsonUtils.toJson(dto)))
            .flatMap(formatRepository::save)
            .then();
    }

}
