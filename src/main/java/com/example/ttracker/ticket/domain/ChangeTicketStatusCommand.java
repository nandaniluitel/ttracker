package com.example.ttracker.ticket.domain;

import com.example.ttracker.security.domain.model.TicketStatus;

public record ChangeTicketStatusCommand(TicketStatus status) {
    public ChangeTicketStatusCommand{
        if(status==null){
            throw new IllegalArgumentException("Ticket status must not be null");
        }
    }
}
