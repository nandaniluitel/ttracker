package com.example.ttracker.application.service;

import com.example.ttracker.security.adapters.in.JwtTokenAdapter;
import com.example.ttracker.security.domain.model.Role;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class JwtTokenAdapterTest {
    public static void main(String[] args) {
        generateToken_andExtractClaims_ok();
        tamperedToken_shouldFail();
        expiredToken_shouldFail();

        System.out.println("✅ JwtTokenAdapter tests passed.");
    }


    static void generateToken_andExtractClaims_ok() {
        String secret = "0123456789_0123456789_0123456789_0123456789";
        long expirationSeconds = 3600;
        JwtTokenAdapter adapter = new JwtTokenAdapter(secret, expirationSeconds);

        Long userId = 42L;
        String email = "user11@test.com";
        Role role = Role.USER;

        //when
        String token = adapter.generateToken(userId, email, role);

        //then
        assertThat(token).isNotBlank();

        assertThat(adapter.extractEmail(token)).isEqualTo(email);
        assertThat(adapter.extractUserId(token)).isEqualTo(userId);
        assertThat(adapter.extractRole(token)).isEqualTo(role);
    }

    static void tamperedToken_shouldFail() {
        // given
        String secret = "0123456789_0123456789_0123456789_0123456789";
        JwtTokenAdapter adapter = new JwtTokenAdapter(secret, 3600);

        String token = adapter.generateToken(1L, "a@test.com", Role.USER);

        // tamper token (change last char)
        String tampered = token.substring(0, token.length() - 1) + (token.endsWith("a") ? "b" : "a");

        // then: any extract should throw because signature check fails
        assertThatThrownBy(() -> adapter.extractEmail(tampered))
                .isInstanceOf(JwtException.class);
    }

    static void expiredToken_shouldFail() {
        // given: expirationSeconds negative => token is immediately expired
        String secret = "0123456789_0123456789_0123456789_0123456789";
        //or simple do
        //String tampered = token + "x"; // append anything
        JwtTokenAdapter adapter = new JwtTokenAdapter(secret, -1);

        String token = adapter.generateToken(1L, "a@test.com", Role.USER);

        // then
        assertThatThrownBy(() -> adapter.extractEmail(token))
                .isInstanceOf(ExpiredJwtException.class);
    }


}
