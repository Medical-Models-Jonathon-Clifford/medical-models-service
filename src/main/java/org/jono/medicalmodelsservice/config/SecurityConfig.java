package org.jono.medicalmodelsservice.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Value("${keySetURI}")
    private String keySetUri;

    @Autowired(required = false)
    private JwtDecoder customJwtDecoder;

    @Bean
    @Order(1)
    public SecurityFilterChain asFilterChain(final HttpSecurity http)
            throws Exception {
        http.cors(cors -> cors
                .configurationSource(createCorsConfig()));

        http.oauth2ResourceServer(resourceServer -> resourceServer
                .jwt(jwt -> {
                    if (customJwtDecoder != null) {
                        jwt.decoder(customJwtDecoder);
                    } else {
                        jwt.jwkSetUri(keySetUri);
                    }
                }));


        http.authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated());

        http.csrf(csrf -> csrf.disable());

        return http.build();
    }

    UrlBasedCorsConfigurationSource createCorsConfig() {
        final var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("authorization", "content-type"));
        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
