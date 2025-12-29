package com.example.ttracker.domain.model;

import java.time.Instant;

public record Notification(
    Long id,
    Long ticketId,
    String message,
    Instant createdAt
) {
}
