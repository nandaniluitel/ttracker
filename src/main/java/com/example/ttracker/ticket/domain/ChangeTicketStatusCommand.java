package com.example.ttracker.ticket.domain;

public record ChangeTicketStatusCommand(TicketStatus status) {
    public ChangeTicketStatusCommand{
        if(status==null){
            throw new IllegalArgumentException("Ticket status must not be null");
        }
    }
}
