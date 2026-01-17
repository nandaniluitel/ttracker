package com.example.ttracker.application.service;

import com.example.ttracker.adapters.out.events.TicketCreatedAfterCommitListener;
import com.example.ttracker.application.port.out.NotificationRepositoryPort;
import com.example.ttracker.domain.event.TicketCreatedEvent;
import com.example.ttracker.domain.model.Notification;
import com.example.ttracker.domain.model.Ticket;
import java.time.Instant;
import org.mockito.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class TicketCreatedAfterCommitListenerTest
{

    public static void main(String[] args) {
        afterCommit_savesNotificationWithCorrectValues();
        System.out.println("✅ Listener unit test passed.");
    }

    static void afterCommit_savesNotificationWithCorrectValues(){
        NotificationRepositoryPort notificationRepository=mock(NotificationRepositoryPort.class);
        TicketCreatedAfterCommitListener listener=new TicketCreatedAfterCommitListener(notificationRepository);

        TicketCreatedEvent event=new TicketCreatedEvent(11L,1L, Instant.now());

        listener.afterCommit(event);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository,times(1)).save(captor.capture());

        Notification saved = captor.getValue();

        assertThat(saved.id()).isNull(); // you create with null id
        assertThat(saved.ticketId()).isEqualTo(11L);
        assertThat(saved.message()).isEqualTo("Ticket Created: id=11");
        assertThat(saved.createdAt()).isNotNull();



    }

}
