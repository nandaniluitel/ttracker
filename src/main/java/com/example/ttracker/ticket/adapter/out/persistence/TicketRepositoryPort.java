package com.example.ttracker.ticket.adapter.out.persistence;

import com.example.ttracker.ticket.domain.TicketFilter;
import com.example.ttracker.security.domain.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepositoryPort {
    Ticket save(Ticket ticket);

    Optional<Ticket> findById(Long id);

    List<Ticket> findAll(TicketFilter filter);
    void deleteById(Long id);

    void clearEpicForTickets(Long epicId);//sets ticket.epic_id=NULL where epic_id=?
    boolean existsBySprintId(Long id);
}
