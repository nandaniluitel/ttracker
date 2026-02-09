package com.example.ttracker.security.domain.model;

import java.time.Instant;

public record Notification(
        Long id,
        Long ticketId,
        String message,
        Instant createdAt
) {
}
