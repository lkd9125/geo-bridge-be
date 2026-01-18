package com.geo.bridge.domain.user.host.repository.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.geo.bridge.domain.user.host.dto.SearchHostDTO;
import com.geo.bridge.domain.user.host.dto.entity.HostDTO;
import com.geo.bridge.domain.user.host.repository.HostBaseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HostBaseRepositoryImpl implements HostBaseRepository{

    private final R2dbcEntityTemplate template;
    private final String TABLE = "HOST";

    @Override
    public Flux<HostDTO> findBySearch(SearchHostDTO searchDTO, Pageable pageable) {
        Criteria criteria = where(searchDTO);

        return template
            .select(HostDTO.class)
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
    public Mono<Long> countBySearch(SearchHostDTO searchDTO) {
        Criteria criteria = where(searchDTO);

        return template
            .count(Query.query(criteria), HostDTO.class);
    }


    private Criteria where(SearchHostDTO searchDTO){
        Criteria criteria = Criteria.empty();

        if(searchDTO.getIdx() != null){
            criteria = criteria.and("IDX").is(searchDTO.getIdx());
        }

        if(searchDTO.getUserIdx() != null){
            criteria = criteria.and("CREATE_AT").is(searchDTO.getUserIdx());
        }

        return criteria;
    }

}
