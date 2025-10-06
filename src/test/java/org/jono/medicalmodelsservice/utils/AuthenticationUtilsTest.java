package org.jono.medicalmodelsservice.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

class AuthenticationUtilsTest {

    private static final AuthenticationUtils authenticationUtils = new AuthenticationUtils();

    @Nested
    @DisplayName("extractCompanyId")
    class ExtractCompanyId {

        @Test
        void shouldReturnCompanyIdWhenPresent() {
            final JwtAuthenticationToken authentication = Mockito.mock(JwtAuthenticationToken.class);
            final Jwt jwt = Mockito.mock(Jwt.class);
            when(authentication.getToken()).thenReturn(jwt);
            when(jwt.getClaims()).thenReturn(Map.of("companyId", "1"));

            assertThat(authenticationUtils.extractCompanyId(authentication, "extract")).isEqualTo("1");
        }

        @Test
        void shouldThrowWhenCompanyIdClaimMissing() {
            final JwtAuthenticationToken authentication = Mockito.mock(JwtAuthenticationToken.class);
            final Jwt jwt = Mockito.mock(Jwt.class);
            when(authentication.getToken()).thenReturn(jwt);
            when(jwt.getClaims()).thenReturn(Map.of());

            assertThrows(IllegalArgumentException.class,
                         () -> authenticationUtils.extractCompanyId(authentication, "extract"));
        }

        @Test
        void shouldThrowWhenCompanyIdClaimIsNull() {
            final JwtAuthenticationToken authentication = Mockito.mock(JwtAuthenticationToken.class);
            final Jwt jwt = Mockito.mock(Jwt.class);
            when(authentication.getToken()).thenReturn(jwt);
            final Map<String, Object> claims = new HashMap<>();
            claims.put("companyId", null);
            when(jwt.getClaims()).thenReturn(claims);

            assertThrows(IllegalArgumentException.class,
                         () -> authenticationUtils.extractCompanyId(authentication, "extract"));
        }
    }

    @Nested
    @DisplayName("extractUserId")
    class ExtractUserId {

        @Test
        void shouldReturnUserIdWhenPresent() {
            final JwtAuthenticationToken authentication = Mockito.mock(JwtAuthenticationToken.class);
            final Jwt jwt = Mockito.mock(Jwt.class);
            when(authentication.getToken()).thenReturn(jwt);
            when(jwt.getClaims()).thenReturn(Map.of("userId", "1"));

            assertThat(authenticationUtils.extractUserId(authentication, "extract")).isEqualTo("1");
        }

        @Test
        void shouldThrowWhenCompanyIdClaimMissing() {
            final JwtAuthenticationToken authentication = Mockito.mock(JwtAuthenticationToken.class);
            final Jwt jwt = Mockito.mock(Jwt.class);
            when(authentication.getToken()).thenReturn(jwt);
            when(jwt.getClaims()).thenReturn(Map.of());

            assertThrows(IllegalArgumentException.class,
                         () -> authenticationUtils.extractUserId(authentication, "extract"));
        }

        @Test
        void shouldThrowWhenCompanyIdClaimIsNull() {
            final JwtAuthenticationToken authentication = Mockito.mock(JwtAuthenticationToken.class);
            final Jwt jwt = Mockito.mock(Jwt.class);
            when(authentication.getToken()).thenReturn(jwt);
            final Map<String, Object> claims = new HashMap<>();
            claims.put("userId", null);
            when(jwt.getClaims()).thenReturn(claims);

            assertThrows(IllegalArgumentException.class,
                         () -> authenticationUtils.extractUserId(authentication, "extract"));
        }
    }

}