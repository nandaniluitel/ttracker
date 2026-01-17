package com.example.ttracker.application.port.out;

import com.example.ttracker.domain.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepositoryPort {
    Ticket save(Ticket ticket);

    Optional<Ticket> findById(Long id);

    List<Ticket> findAll();
}
