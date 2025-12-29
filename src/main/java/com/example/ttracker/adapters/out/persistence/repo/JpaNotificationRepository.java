package com.example.ttracker.adapters.out.persistence.repo;

import com.example.ttracker.adapters.out.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaNotificationRepository extends JpaRepository<NotificationEntity,Long> {
}
