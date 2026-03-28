package com.example.ttracker.ticket.adapter.out.persistence;

import com.example.ttracker.ticket.domain.TicketHistory;

public interface TicketHistoryRepositoryPort {
    TicketHistory save(TicketHistory history);
}
