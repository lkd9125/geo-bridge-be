package com.geo.bridge.domain.simulator.history.repository.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;

import com.geo.bridge.domain.simulator.history.dto.entity.History;
import com.geo.bridge.domain.simulator.history.repository.HistoryBaseRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository 
@RequiredArgsConstructor 
public class HistoryBaseRepositoryImpl implements HistoryBaseRepository {

    private final R2dbcEntityTemplate template;
    private final String TABLE = "HISTORY";

    @Override
    public Flux<History> findByUsername(String username, Pageable pageable) {
        Criteria criteria = Criteria.where("USERNAME").is(username);

        return template
            .select(History.class)
            .from(this.TABLE)
            .matching(
                Query.query(criteria)
                    .sort(
                        Sort.by(
                            Sort.Order.desc("FAVORITE_YN"),
                            Sort.Order.desc("FAVORITE_DT"),
                            Sort.Order.desc("CREATE_DT")
                        )
                    )
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
            )
            .all();
    }

    @Override
    public Mono<Long> countByUsername(String username) {
        Criteria criteria = Criteria.where("USERNAME").is(username);

        return template.count(Query.query(criteria), History.class);
    }


}
