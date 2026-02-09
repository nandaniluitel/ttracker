package com.example.ttracker.ticket.adapter.out.events;

import com.example.ttracker.security.domain.event.TicketCreatedEvent;
import com.example.ttracker.security.domain.model.Notification;
import com.example.ttracker.ticket.adapter.out.persistence.NotificationRepositoryPort;
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
