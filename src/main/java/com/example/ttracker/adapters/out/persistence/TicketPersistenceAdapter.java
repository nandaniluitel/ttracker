package com.example.ttracker.adapters.out.persistence;

import com.example.ttracker.adapters.out.persistence.entity.TicketEntity;
import com.example.ttracker.adapters.out.persistence.repo.JpaTicketRepository;
import com.example.ttracker.application.port.out.TicketRepositoryPort;
import com.example.ttracker.domain.model.Ticket;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TicketPersistenceAdapter implements TicketRepositoryPort {
    private final JpaTicketRepository jpa;

    public TicketPersistenceAdapter(JpaTicketRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity saved = jpa.save(TicketEntity.from(ticket));
        return saved.toDomain();
    }

    @Override
    public Optional<Ticket> findById(Long id) {
        return jpa.findById(id).map(TicketEntity::toDomain);
    }

    @Override
    public List<Ticket> findAll() {
        return jpa.findAll().stream().map(TicketEntity::toDomain).toList();
    }

}
