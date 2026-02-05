package com.example.ttracker.application.port.in.tickets;

import com.example.ttracker.domain.model.TicketStatus;

public record ChangeTicketStatusCommand(TicketStatus status) {
}
