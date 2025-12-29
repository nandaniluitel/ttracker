package com.example.ttracker.adapters.out.persistence.repo;

import com.example.ttracker.adapters.out.persistence.entity.TicketHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTicketHistoryRepository extends JpaRepository<TicketHistoryEntity,Long> {
}
