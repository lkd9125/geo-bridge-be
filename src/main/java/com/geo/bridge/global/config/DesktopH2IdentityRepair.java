package com.geo.bridge.global.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * H2 파일 DB에서 AUTO_INCREMENT/IDENTITY 시퀀스가 테이블 MAX(IDX)와 어긋나면
 * 다음 INSERT가 이미 존재하는 PK와 충돌한다. 데스크톱 프로필 기동 시 한 번 맞춘다.
 */
@Component
@Profile("desktop")
@RequiredArgsConstructor
@Slf4j
public class DesktopH2IdentityRepair {

    private final ConnectionFactory connectionFactory;

    @EventListener(ApplicationReadyEvent.class)
    public void syncIdentityColumns() {
        DatabaseClient client = DatabaseClient.create(connectionFactory);
        restartWithMaxPlusOne(client, "HOST", "IDX");
        restartWithMaxPlusOne(client, "FORMAT", "IDX");
    }

    private void restartWithMaxPlusOne(DatabaseClient client, String table, String column) {
        try {
            Long next = client.sql("SELECT COALESCE(MAX(" + column + "), 0) + 1 AS n FROM " + table)
                .map((row, meta) -> row.get("n", Long.class))
                .one()
                .block();
            if (next == null) {
                next = 1L;
            }
            client.sql("ALTER TABLE " + table + " ALTER COLUMN " + column + " RESTART WITH " + next)
                .fetch()
                .rowsUpdated()
                .block();
            log.info("H2 identity {}.{} -> RESTART WITH {}", table, column, next);
        } catch (Exception e) {
            log.warn("H2 identity sync skipped for {}.{}: {}", table, column, e.getMessage());
        }
    }
}
