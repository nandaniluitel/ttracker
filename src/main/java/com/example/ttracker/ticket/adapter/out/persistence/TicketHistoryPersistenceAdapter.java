package com.example.ttracker.ticket.adapter.out.persistence;

import com.example.ttracker.ticket.domain.TicketHistory;
import org.springframework.stereotype.Component;

@Component
public class TicketHistoryPersistenceAdapter implements TicketHistoryRepositoryPort {
    private final JpaTicketHistoryRepository jpa;

    public TicketHistoryPersistenceAdapter(JpaTicketHistoryRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public TicketHistory save(TicketHistory history) {
        TicketHistoryEntity saved = jpa.save(TicketHistoryEntity.from(history));
        return saved.toDomain();
    }
}
