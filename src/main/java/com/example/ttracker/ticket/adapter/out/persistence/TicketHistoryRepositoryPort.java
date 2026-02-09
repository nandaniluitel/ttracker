package com.example.ttracker.ticket.adapter.out.persistence;

import com.example.ttracker.security.domain.model.TicketHistory;

public interface TicketHistoryRepositoryPort {
    TicketHistory save(TicketHistory history);
}
