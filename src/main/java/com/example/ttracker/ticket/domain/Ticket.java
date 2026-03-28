package com.example.ttracker.ticket.domain;

import com.example.ttracker.security.domain.model.Priority;
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
