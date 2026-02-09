package com.example.ttracker.ticket.adapter.out.persistence;

import com.example.ttracker.security.domain.model.Notification;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);
}
