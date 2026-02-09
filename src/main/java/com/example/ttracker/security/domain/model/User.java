package com.example.ttracker.security.domain.model;

import java.time.Instant;

public record User(
        Long id,
        String email,
        String passwordHash,
        Role role,
        Instant createdAt

) {
}
