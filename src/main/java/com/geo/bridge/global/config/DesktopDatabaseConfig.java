package com.geo.bridge.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Profile("desktop")
@Slf4j
@Lazy(false)
public class DesktopDatabaseConfig {

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        
        CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
        populator.addPopulators(
            new ResourceDatabasePopulator(new ClassPathResource("db/h2/ddl.sql"))
        );
        initializer.setDatabasePopulator(populator);
        
        return initializer;
    }

}
