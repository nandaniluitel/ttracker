package com.example.ttracker.security.domain.model;

import java.time.Instant;
import java.time.LocalDate;

public record Sprint(
    Long id,
    String title,
    String goal,
    LocalDate startDate,
    LocalDate endDate,
    SprintStatus status,
    Long createdByUserId,
    Long editedByUserId,
    Instant createdAt,
    Instant updatedAt
) {
}
