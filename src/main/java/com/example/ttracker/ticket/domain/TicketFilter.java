package com.example.ttracker.ticket.domain;

import com.example.ttracker.security.domain.model.Priority;

public record TicketFilter(
    Long sprintId,
    Long epicId,
    Long assigneeUserId,
    TicketStatus status,
    Priority priority
) {
}
