package com.example.ttracker.adapters.out.events;

import com.example.ttracker.application.port.out.NotificationRepositoryPort;
import com.example.ttracker.domain.event.TicketCreatedEvent;
import com.example.ttracker.domain.model.Notification;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TicketCreatedAfterCommitListener {
    private final NotificationRepositoryPort notificationRepositoryPort;
    private static final Logger log = LoggerFactory.getLogger(TicketCreatedAfterCommitListener.class);

    public TicketCreatedAfterCommitListener(NotificationRepositoryPort notificationRepositoryPort) {
        this.notificationRepositoryPort = notificationRepositoryPort;
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(TicketCreatedEvent ticketCreatedEvent)
    {
        log.info("eventListener:about to create notification");
        Notification notification= new Notification(
            null,
            ticketCreatedEvent.ticketId(),
            "Ticket Created: id="+ticketCreatedEvent.ticketId(),
            Instant.now());
        log.info("notification{}",notification);
        notificationRepositoryPort.save(notification);
    }

}
