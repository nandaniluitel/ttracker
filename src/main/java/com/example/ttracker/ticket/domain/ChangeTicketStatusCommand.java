package com.example.ttracker.ticket.domain;

import com.example.ttracker.security.domain.model.TicketStatus;

public record ChangeTicketStatusCommand(TicketStatus status) {
}
