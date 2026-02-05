package com.example.ttracker.domain.model;

import java.time.Instant;

public record Epic(
    Long id,
    String title,
    String description,
    EpicStatus status,
    Priority priority,
    Long assigneeUserId,
    Long createdByUserId,
    Long editedByUsedId,
    Instant createdAt,
    Instant updatedAt

) {
}
