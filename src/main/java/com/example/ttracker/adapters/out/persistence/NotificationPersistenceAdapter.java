package com.example.ttracker.adapters.out.persistence;

import com.example.ttracker.adapters.out.persistence.entity.NotificationEntity;
import com.example.ttracker.adapters.out.persistence.repo.JpaNotificationRepository;
import com.example.ttracker.application.port.out.NotificationRepositoryPort;
import com.example.ttracker.domain.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationPersistenceAdapter implements NotificationRepositoryPort {
    private final JpaNotificationRepository jpa;

    public NotificationPersistenceAdapter(JpaNotificationRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = NotificationEntity.from(notification);
        NotificationEntity persisted = jpa.save(entity);
        return new Notification(persisted.getId(), persisted.getTicketId(), persisted.getMessage(), persisted.getCreatedAt());

    }
}