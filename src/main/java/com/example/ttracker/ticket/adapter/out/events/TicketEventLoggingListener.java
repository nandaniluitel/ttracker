package com.example.ttracker.ticket.adapter.out.events;

import com.example.ttracker.security.domain.event.TicketCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TicketEventLoggingListener {
    private static final Logger log = LoggerFactory.getLogger(TicketEventLoggingListener.class);

    @EventListener
    public void onTicketCreated(TicketCreatedEvent event) {
        log.info("Event: TicketCreatedEvent ticketId={} createdBy={} at={}", event.ticketId(), event.createdByUserId(), event.createdAt());
    }
}
