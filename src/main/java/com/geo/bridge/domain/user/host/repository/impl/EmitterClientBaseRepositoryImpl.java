package com.geo.bridge.domain.user.host.repository.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.geo.bridge.domain.user.host.dto.SearchEmitterDTO;
import com.geo.bridge.domain.user.host.dto.entity.ClientHostDTO;
import com.geo.bridge.domain.user.host.repository.EmitterClientBaseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EmitterClientBaseRepositoryImpl implements EmitterClientBaseRepository{

    private final R2dbcEntityTemplate template;
    private final String TABLE = "CLIENT_HOST";

    @Override
    public Flux<ClientHostDTO> findBySearch(SearchEmitterDTO searchDTO, Pageable pageable) {
        Criteria criteria = where(searchDTO);

        return template
            .select(ClientHostDTO.class)
            .from(this.TABLE)
            .matching(
                Query.query(criteria)
                    .sort(Sort.by("CREATE_DT").descending())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
            )
            .all();
    }

    @Override
    public Mono<Long> countBySearch(SearchEmitterDTO searchDTO) {
        Criteria criteria = where(searchDTO);

        return template
            .count(Query.query(criteria), ClientHostDTO.class);
    }


    private Criteria where(SearchEmitterDTO searchDTO){
        Criteria criteria = Criteria.empty();

        if(searchDTO.getIdx() != null){
            criteria.and("IDX").is(searchDTO.getIdx());
        }
        return criteria;
    }

}
