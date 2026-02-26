package com.geo.bridge.domain.user.host;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.geo.bridge.domain.user.host.dto.SearchHostDTO;
import com.geo.bridge.domain.user.host.dto.entity.HostDTO;
import com.geo.bridge.domain.user.host.repository.HostRepository;
import com.geo.bridge.global.base.BasePageRQ;
import com.geo.bridge.global.base.BasePageRS;
import com.geo.bridge.global.exception.BaseException;
import com.geo.bridge.global.exception.ExceptionCode;
import com.geo.bridge.global.utils.JsonUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * HOST 서비스
 * <p>기능</p>
 * <ul>
 *     <li>{@link #createClientHost(Mono)} DB HOST 데이터 Save</li>
 *     <li>{@link #getAllClientHost(SearchHostDTO, BasePageRQ)} DB HOST Search</li>
 *     <li>{@link #deleteClientHost(Long)} DB HOST 삭제</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class HostService {

    private final HostRepository hostRepository;

    /**
     * DB HOST 저장
     * @param mono 생성객체
     * @return
     */
    public Mono<Void> createClientHost(Mono<HostDTO> mono){
        return mono
            .doOnNext(dto -> log.info(JsonUtils.toJson(dto)))
            .flatMap(hostRepository::save)
            .onErrorResume(throwable -> {
                log.warn("createClientHost error :: {}", throwable.getMessage());
                if (throwable instanceof BaseException baseEx) {
                    return Mono.error(baseEx);
                }
                return Mono.error(new BaseException(ExceptionCode.SERVER_INVALID, throwable.getMessage(), throwable));
            })
            .then();
    }

    /**
     * DB HOST 검색(페이징)
     * @param searchDTO 검색객체
     * @param pageable 페이지네이션
     * @return
     */
    public Mono<BasePageRS<HostDTO>> getAllClientHost(SearchHostDTO searchDTO, BasePageRQ page){
        Pageable pageable = PageRequest.of(page.getPage() - 1, page.getSize());

        Mono<List<HostDTO>> listMono = hostRepository.findBySearch(searchDTO, pageable).collectList();
        Mono<Long> cntMono = hostRepository.countBySearch(searchDTO);

        return Mono.zip(listMono, cntMono)
            .map(tuple -> {
                List<HostDTO> list = tuple.getT1();
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

    /**
     * DB HOST 삭제
     * @param idx HOST PK
     * @return
     */
    public Mono<Void> deleteClientHost(Long idx){
        return hostRepository.deleteById(idx);
    }
    
}
