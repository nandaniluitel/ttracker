package com.example.ttracker.adapters.out.persistence;

import com.example.ttracker.adapters.out.persistence.entity.TicketHistoryEntity;
import com.example.ttracker.adapters.out.persistence.repo.JpaTicketHistoryRepository;
import com.example.ttracker.adapters.out.persistence.repo.JpaTicketRepository;
import com.example.ttracker.application.port.out.TicketHistoryRepositoryPort;
import com.example.ttracker.domain.model.TicketHistory;
import org.springframework.stereotype.Component;

@Component
public class TicketHistoryPersistenceAdapter implements TicketHistoryRepositoryPort {
    private final JpaTicketHistoryRepository jpa;

    public TicketHistoryPersistenceAdapter(JpaTicketHistoryRepository jpa) {
        this.jpa = jpa;
    }

    @Override public TicketHistory save(TicketHistory history) {
     TicketHistoryEntity saved= jpa.save(TicketHistoryEntity.from(history)) ;
     return saved.toDomain();
    }
}
