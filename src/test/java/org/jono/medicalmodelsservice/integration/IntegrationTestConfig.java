package org.jono.medicalmodelsservice.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;


@TestConfiguration
public class IntegrationTestConfig {
    @Bean
    @Primary
    JwtDecoder testJwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(TestJwtUtils.getPublicKey())
                .build();
    }
}
