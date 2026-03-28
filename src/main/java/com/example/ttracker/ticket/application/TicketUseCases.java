package com.example.ttracker.ticket.application;

import com.example.ttracker.ticket.domain.Ticket;
import com.example.ttracker.ticket.domain.TicketStatus;
import com.example.ttracker.ticket.domain.CreateTicketCommand;
import com.example.ttracker.ticket.domain.TicketFilter;
import com.example.ttracker.ticket.domain.UpdateTicketCommand;
import java.util.List;

public interface TicketUseCases {
    Ticket create(CreateTicketCommand command);

    Ticket getById(Long id);
    Boolean existBySprintId(Long sprintId);

    List<Ticket> list();

    List<Ticket> list(TicketFilter filter);

    Ticket update(Long ticketId, UpdateTicketCommand command);//non-status field

    Ticket changeStatus(Long ticketId, TicketStatus newStatus);

    void delete(Long ticketId); // optional (ADMIN only)
}