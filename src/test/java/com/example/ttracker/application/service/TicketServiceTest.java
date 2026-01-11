package com.example.ttracker.application.service;

import com.example.ttracker.application.port.out.CurrentUserPort;
import com.example.ttracker.application.port.out.EventPublisherPort;
import com.example.ttracker.application.port.out.TicketHistoryRepositoryPort;
import com.example.ttracker.application.port.out.TicketRepositoryPort;
import com.example.ttracker.domain.model.Ticket;

import static org.mockito.Mockito.*;


public class TicketServiceTest {
    public static void main(String[] args){

    }
    static class Fixture{
        final TicketRepositoryPort ticketRepository=mock(TicketRepositoryPort.class);
        final TicketHistoryRepositoryPort ticketHistoryRepository=mock(TicketHistoryRepositoryPort.class);
        final CurrentUserPort currentUser=mock(CurrentUserPort.class);
        final EventPublisherPort eventPublisher=mock(EventPublisherPort.class);
        final TicketService service=new TicketService(
            ticketRepository,
            ticketHistoryRepository,
            currentUser,
            eventPublisher
        );
    }
    static void create_savesTicketHistory_PublishesEvent(){
        Fixture f=new Fixture();
        //stud
        when(f.currentUser.currentUserId()).thenReturn(42L);
        when(f.ticketRepository.save(any(Ticket.class))).thenAnswer(inv->{
            Ticket t=inv.getArgument(0);
            return new Ticket(
                400L,
                t.title();
                t.description();
                t.status();
                t.createdAt();
                t.createdByUserId()
            );
        });


    }
}
