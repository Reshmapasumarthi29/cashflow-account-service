package com.belenits.cashflow.account.security;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {

    public Long extractUserId(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or malformed Authorization header");
        }

        String token = authHeader.substring(7);

        // Decode-only: signature already verified upstream by the Gateway,
        // so we parse the claims without re-verifying here.
        Claims claims = Jwts.parser()
                .unsecured()
                .build()
                .parseUnsecuredClaims(token)
                .getPayload();

        return claims.get("userId", Long.class);
    }
}