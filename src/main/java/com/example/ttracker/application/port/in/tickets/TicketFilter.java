package com.example.ttracker.application.port.in.tickets;

import com.example.ttracker.domain.model.Priority;
import com.example.ttracker.domain.model.TicketStatus;

public record TicketFilter(
    Long sprintId,
    Long epicId,
    Long assigneeUserId,
    TicketStatus status,
    Priority priority
) {
}
