package com.example.ttracker.application.port.in;

import com.example.ttracker.domain.model.Ticket;
import com.example.ttracker.domain.model.TicketStatus;

import java.util.List;

public interface TicketUseCases {
    Ticket create(CreateTicketCommand command);

    Ticket getById(Long id);

    List<Ticket> list();

    Ticket changeStatus(Long ticketId, TicketStatus newStatus);
}
