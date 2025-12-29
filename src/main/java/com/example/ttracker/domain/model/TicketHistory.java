package com.example.ttracker.domain.model;

import java.time.Instant;

public record TicketHistory(
    Long id,
    Long ticketId,
    TicketHistoryAction action,
    TicketStatus oldStatus,
    TicketStatus newStatus,
    Instant changedAt,
    Long changedByUserId


) {
}
