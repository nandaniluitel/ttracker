package com.example.ttracker.ticket.adapter.out.persistence;

import com.example.ttracker.security.domain.model.Notification;
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