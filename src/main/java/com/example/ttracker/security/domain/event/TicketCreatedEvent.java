package com.example.ttracker.security.domain.event;

import java.time.Instant;

public record TicketCreatedEvent(Long ticketId, Long createdByUserId, Instant createdAt) {
}
