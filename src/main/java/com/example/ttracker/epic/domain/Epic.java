package com.example.ttracker.epic.domain;

import com.example.ttracker.security.domain.model.Priority;
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
