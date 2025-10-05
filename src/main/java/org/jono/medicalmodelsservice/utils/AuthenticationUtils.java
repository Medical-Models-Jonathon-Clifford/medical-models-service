package org.jono.medicalmodelsservice.utils;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public final class AuthenticationUtils {

    public static final String COMPANY_ID_CLAIM = "companyId";
    public static final String USER_ID_CLAIM = "userId";

    private AuthenticationUtils() {
        // Utility class
    }

    public static String extractCompanyId(final JwtAuthenticationToken authentication, final String operation) {
        if (authentication.getToken().getClaims().get(COMPANY_ID_CLAIM) instanceof String companyId) {
            return companyId;
        }
        throw new IllegalArgumentException(COMPANY_ID_CLAIM + " is required to " + operation);
    }

    public static String extractUserId(final JwtAuthenticationToken authentication, final String operation) {
        if (authentication.getToken().getClaims().get(USER_ID_CLAIM) instanceof String userId) {
            return userId;
        }
        throw new IllegalArgumentException(USER_ID_CLAIM + " is required to " + operation);
    }
}
