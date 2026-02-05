package com.example.ttracker.ticket.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
