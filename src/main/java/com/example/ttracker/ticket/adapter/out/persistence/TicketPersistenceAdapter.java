package com.example.ttracker.ticket.adapter.out.persistence;

import com.example.ttracker.ticket.domain.TicketFilter;
import com.example.ttracker.security.domain.model.Ticket;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

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


    @Override public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Transactional
    @Override public void clearEpicForTickets(Long epicId) {
        jpa.clearEpicForTickets(epicId);
    }

    @Override public boolean existsBySprintId(Long id) {
        return jpa.existsBySprintId(id);
    }

    @Override
    public List<Ticket> findAll(TicketFilter filter) {
        if (filter == null) {
            return jpa.findAll().stream().map(TicketEntity::toDomain).toList();
        }
        return jpa.search(filter.sprintId(), filter.epicId(),filter.assigneeUserId(),filter.status(),filter.priority()).stream().map(TicketEntity::toDomain).toList();
    }

}
