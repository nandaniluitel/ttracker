package com.example.ttracker.adapters.out.events;

import com.example.ttracker.application.port.out.NotificationRepositoryPort;
import com.example.ttracker.domain.event.TicketCreatedEvent;
import com.example.ttracker.domain.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;

@Component
public class TicketCreatedListener {
    private static final Logger log = LoggerFactory.getLogger(TicketCreatedListener.class);
    private final NotificationRepositoryPort notificationRepositoryPort;

    public TicketCreatedListener(NotificationRepositoryPort notificationRepositoryPort) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(TicketCreatedEvent ticketCreatedEvent) {
        log.info("eventListener:about to create notification");
        Notification notification = new Notification(
                null,
                ticketCreatedEvent.ticketId(),
                "Ticket Created: id=" + ticketCreatedEvent.ticketId(),
                Instant.now());
        log.info("notification{}", notification);
        notificationRepositoryPort.save(notification);
    }

}
