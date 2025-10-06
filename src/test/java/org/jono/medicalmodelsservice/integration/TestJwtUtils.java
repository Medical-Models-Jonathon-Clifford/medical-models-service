package org.jono.medicalmodelsservice.integration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TestJwtUtils {
    private static final KeyPair KEY_PAIR;
    private static final String ISSUER = "http://localhost:7071";
    private static final String AUDIENCE = "next-auth-client";
    private static final int DAYS_100_YEARS = 36500;
    private static final int KEY_SIZE = 2048;

    static {
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(KEY_SIZE);
            KEY_PAIR = keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException("Error generating key pair", e);
        }
    }

    public static String createTestJwtToken(final String subject, final String email, final String companyId) {
        final long now = System.currentTimeMillis();
        final Date expiration = new Date(now + TimeUnit.DAYS.toMillis(DAYS_100_YEARS)); // 100 years

        return Jwts.builder()
                .setHeaderParam("kid", UUID.randomUUID().toString())
                .setHeaderParam("typ", "JWT")
                .setSubject(subject)
                .setIssuer(ISSUER)
                .setAudience(AUDIENCE)
                .setIssuedAt(new Date(now))
                .setExpiration(expiration)
                .setId(UUID.randomUUID().toString())
                .claim("email", email)
                .claim("email_verified", true)
                .claim("companyId", companyId)
                .claim("scope", "openid profile email")
                .claim("roles", List.of("ROLE_ADMIN"))
                .signWith(KEY_PAIR.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }

    public static RSAPublicKey getPublicKey() {
        return (RSAPublicKey) KEY_PAIR.getPublic();
    }
}
