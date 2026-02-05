package com.example.ttracker.security.domain.model;

import java.time.Instant;

public record Ticket(
        Long id,
        String title,
        String description,
        TicketStatus status,
        Priority priority,
        Integer storyPoints,
        Long assigneeUserId,
        Long epicId,
        Long sprintId,
        Long createdByUserId,
        Long editedByUserId,
        Instant createdAt,
        Instant updatedAt
) {

}
