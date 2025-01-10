package org.jono.medicalmodelsservice.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

@Configuration
public class DbConfig extends AbstractR2dbcConfiguration {

    @Value("${db.user}")
    private String dbUser;

    @Value("${db.password}")
    private String dbPassword;

    @Value("${db.host}")
    private String dbHost;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        return ConnectionFactories.get(String.format("r2dbc:mysql://%s:%s@%s/capa?zeroDateTimeBehavior=CONVERT_TO_NULL", dbUser, dbPassword, dbHost));
    }
}