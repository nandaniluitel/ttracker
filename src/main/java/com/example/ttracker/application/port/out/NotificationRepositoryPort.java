package com.example.ttracker.application.port.out;

import com.example.ttracker.domain.model.Notification;

public interface NotificationRepositoryPort {
    Notification save(Notification notification);
}
