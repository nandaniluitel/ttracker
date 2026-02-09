package com.example.ttracker.ticket.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTicketHistoryRepository extends JpaRepository<TicketHistoryEntity, Long> {
}
