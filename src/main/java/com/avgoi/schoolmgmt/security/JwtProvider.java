package com.avgoi.schoolmgmt.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    public static final String CLAIM_TENANT_ID = "tenantId";
    public static final String CLAIM_PERMISSIONS = "permissions";
    public static final String CLAIM_EMAIL = "sub";

    private final SecretKey key;
    private final long expirationMs;

    public JwtProvider(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiration-ms:86400000}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(CustomUserDetails userDetails) {
        List<String> permissions = userDetails.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(CLAIM_TENANT_ID, userDetails.getTenantId())
                .claim(CLAIM_PERMISSIONS, permissions)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getPermissionsFromToken(String token) {
        return (List<String>) parseClaims(token).get(CLAIM_PERMISSIONS, List.class);
    }

    public String getTenantIdFromToken(String token) {
        return parseClaims(token).get(CLAIM_TENANT_ID, String.class);
    }
}
