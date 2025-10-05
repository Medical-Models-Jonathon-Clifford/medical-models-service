package org.jono.medicalmodelsservice.utils;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class AuthenticationUtils {

    private AuthenticationUtils() {
        // Utility class
    }

    public static String extractCompanyId(final JwtAuthenticationToken authentication, final String operation) {
        if (authentication.getToken().getClaims().get("companyId") instanceof String companyId) {
            return companyId;
        }
        throw new IllegalArgumentException("companyId is required to " + operation);
    }
}
