package com.example.ttracker.adapters.in;

import com.example.ttracker.application.port.out.TokenPort;
import com.example.ttracker.domain.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenAdapter implements TokenPort {
    private final SecretKey key;
    private final long expirationSeconds;

    public JwtTokenAdapter(@Value("${security.jwt.secret}") String secret,
                            @Value("${security.jwt.expiration-seconds}") long expirationSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    @Override public String generateToken(Long userId, String email, Role role) {
        Instant now=Instant.now();
        Instant exp=now.plusSeconds(expirationSeconds);
        return Jwts.builder()
            .subject(email)
            .claims(Map.of(
                "uid",userId,
                "role",role.name()
            ))
            .issuedAt(Date.from(now))
            .expiration(Date.from(exp))
            .signWith(key)
            .compact();
    }
    private Jws<Claims> parse(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token);
    }

    @Override public Long extractUserId(String token) {
        Object uid=parse(token).getPayload().get("uid");
        if(uid==null)return null;
        if(uid instanceof Number n) return n.longValue();
        return Long.valueOf(uid.toString());

    }

    @Override public String extractEmail(String token) {
       return parse(token).getPayload().getSubject();
    }

    @Override public Role extractRole(String token) {
        Object role=parse(token).getPayload().get("role");
        return role==null?null:Role.valueOf(role.toString());

    }
}
