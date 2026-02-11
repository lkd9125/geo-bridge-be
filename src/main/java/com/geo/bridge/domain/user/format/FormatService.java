package com.geo.bridge.domain.user.format;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.geo.bridge.domain.user.format.dto.SearchFormatDTO;
import com.geo.bridge.domain.user.format.dto.entity.FormatDTO;
import com.geo.bridge.domain.user.format.repository.FormatRepository;
import com.geo.bridge.global.base.BasePageRQ;
import com.geo.bridge.global.base.BasePageRS;
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

    public Mono<BasePageRS<FormatDTO>> getAllFormat(SearchFormatDTO searchFormatDTO, BasePageRQ page){
        Pageable pageable = PageRequest.of(page.getPage() - 1, page.getSize());

        Mono<List<FormatDTO>> listMono = formatRepository.findBySearch(searchFormatDTO, pageable).collectList();
        Mono<Long> cntMono = formatRepository.countBySearch(searchFormatDTO);
        
        return Mono.zip(listMono, cntMono)
            .map(tuple -> {
                List<FormatDTO> list = tuple.getT1();
                Long cnt = tuple.getT2();

                return BasePageRS.of(
                    list, 
                    page.getPage(), 
                    page.getSize(), 
                    page.getRange(), 
                    cnt.intValue()
                );
            });
    }

}
