package com.example.ttracker.adapters.out.persistence.entity;

import com.example.ttracker.domain.model.Notification;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public NotificationEntity() {
    }

    public static NotificationEntity from(Notification notification) {
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setId(notification.id());
        notificationEntity.setTicketId(notification.ticketId());
        notificationEntity.setMessage(notification.message());
        notificationEntity.setCreatedAt(notification.createdAt());
        return notificationEntity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}


