package com.example.ttracker.ticket.domain;

import com.example.ttracker.security.domain.model.Priority;
import com.example.ttracker.security.domain.model.TicketStatus;

public record TicketFilter(
    Long sprintId,
    Long epicId,
    Long assigneeUserId,
    TicketStatus status,
    Priority priority
) {
}
