package com.example.ttracker.security.domain.event;

import java.time.Instant;

public record TicketCreatedEvent(Long ticketId, Long createdByUserId, Instant createdAt) {
    public TicketCreatedEvent{
        if (ticketId == null ) {
            throw new IllegalArgumentException("TicketId  must not be null");
        }
        if (createdByUserId == null ) {
            throw new IllegalArgumentException("Ticket user Id must not be null");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Ticket createdAt  must not be null");
        }
    }
}
