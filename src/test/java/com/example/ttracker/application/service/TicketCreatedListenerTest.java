package com.example.ttracker.application.service;

import com.example.ttracker.ticket.adapter.out.events.TicketCreatedListener;
import com.example.ttracker.ticket.adapter.out.persistence.NotificationRepositoryPort;
import com.example.ttracker.security.domain.event.TicketCreatedEvent;
import com.example.ttracker.security.domain.model.Notification;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

public class TicketCreatedListenerTest extends MySqlTestcontainerBase{

    private final NotificationRepositoryPort notificationRepository = mock(NotificationRepositoryPort.class);
    private final TicketCreatedListener listener = new TicketCreatedListener(notificationRepository);


    @Test
    public void afterCommit_savesNotificationWithCorrectValues() {

        //given
        TicketCreatedEvent event = new TicketCreatedEvent(11L, 1L, Instant.now());

        //when
        listener.afterCommit(event);

        //then
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(captor.capture());

        Notification saved = captor.getValue();

        assertThat(saved.id()).isNull(); // you create with null id
        assertThat(saved.ticketId()).isEqualTo(11L);
        assertThat(saved.message()).isEqualTo("Ticket Created: id=11");
        assertThat(saved.createdAt()).isNotNull();


    }

}
