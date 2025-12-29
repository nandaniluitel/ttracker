package com.example.ttracker.application.port.out;

import com.example.ttracker.domain.model.TicketHistory;

public interface TicketHistoryRepositoryPort {
    TicketHistory save(TicketHistory history);
}
