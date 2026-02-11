package com.geo.bridge.domain.user.format.repository.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.geo.bridge.domain.user.format.dto.SearchFormatDTO;
import com.geo.bridge.domain.user.format.dto.entity.FormatDTO;
import com.geo.bridge.domain.user.format.repository.FormatBaseRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FormatBaseRepositoryImpl implements FormatBaseRepository{

    private final R2dbcEntityTemplate template;
    private final String TABLE = "FORMAT";

    
    @Override
    public Flux<FormatDTO> findBySearch(SearchFormatDTO searchDTO, Pageable pageable) {
        Criteria criteria = where(searchDTO);
        
        return template
            .select(FormatDTO.class)
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
    public Mono<Long> countBySearch(SearchFormatDTO searchDTO) {
        Criteria criteria = where(searchDTO);
        
        return template
            .count(Query.query(criteria), FormatDTO.class);
    }

    private Criteria where(SearchFormatDTO searchDTO){
        Criteria criteria = Criteria.empty();

        if(searchDTO.getIdx() != null){
            criteria = criteria.and("IDX").is(searchDTO.getIdx());
        }

        if(searchDTO.getUserId() != null){
            criteria = criteria.and("CREATE_AT").is(searchDTO.getUserId());
        }

        return criteria;
    }

}
