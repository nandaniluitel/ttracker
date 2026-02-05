package com.example.ttracker.application.port.in.tickets;

import com.example.ttracker.domain.model.Ticket;
import com.example.ttracker.domain.model.TicketStatus;

import java.util.List;

public interface TicketUseCases {
    Ticket create(CreateTicketCommand command);

    Ticket getById(Long id);

    List<Ticket> list(TicketFilter filter);
    Ticket update(Long ticketId, UpdateTicketCommand command);//non-status field

    Ticket changeStatus(Long ticketId, TicketStatus newStatus);
    void delete(Long ticketId); // optional (ADMIN only)
}
