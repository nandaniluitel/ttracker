package com.example.ttracker.application.service;

import com.example.ttracker.application.port.in.CreateTicketCommand;
import com.example.ttracker.application.port.out.CurrentUserPort;
import com.example.ttracker.application.port.out.EventPublisherPort;
import com.example.ttracker.application.port.out.TicketHistoryRepositoryPort;
import com.example.ttracker.application.port.out.TicketRepositoryPort;
import com.example.ttracker.domain.event.TicketCreatedEvent;
import com.example.ttracker.domain.model.Role;
import com.example.ttracker.domain.model.Ticket;
import com.example.ttracker.domain.model.TicketHistory;
import com.example.ttracker.domain.model.TicketHistoryAction;
import com.example.ttracker.domain.model.TicketStatus;
import java.time.Instant;
import java.util.Optional;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;


public class TicketServiceTest {
    public static void main(String[] args){
        create_savesTicketHistory_PublishesEvent();
        changeStatus_userCannotChangeOthers_throwsForbidden();
        changeStatus_userCanChange();
        changeStatus_adminCanChangeany();
        changeStatus_ticketNotFound_throws_andDoesNotSaveAnything();
        System.out.println("Testing doneeee");
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
                100L,
                t.title(),
                t.description(),
                t.status(),
                t.createdAt(),
                t.createdByUserId()
            );
        });
        //act
        Ticket saved=f.service.create(new CreateTicketCommand(" Ticket test 1 "," Desc "));
        //assert
        assertThat(saved.id()).isEqualTo(100L);
        assertThat(saved.title()).isEqualTo("Ticket test 1");
        assertThat(saved.description()).isEqualTo("Desc");
        assertThat(saved.status()).isEqualTo(TicketStatus.OPEN);
        assertThat(saved.createdByUserId()).isEqualTo(42L);


        //verify  ticketRepo was called with trimmed data
        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(f.ticketRepository,times(1)).save(ticketCaptor.capture());
        Ticket ticketToSave=ticketCaptor.getValue();
        assertThat(ticketToSave.id()).isNull();
        assertThat(ticketToSave.title()).isEqualTo("Ticket test 1");
        assertThat(ticketToSave.description()).isEqualTo("Desc");
        assertThat(ticketToSave.status()).isEqualTo(TicketStatus.OPEN);
        assertThat(ticketToSave.createdByUserId()).isEqualTo(42L);
        //verify history saved
        ArgumentCaptor<TicketHistory> histCaptor = ArgumentCaptor.forClass(TicketHistory.class);
        verify(f.ticketHistoryRepository,times(1)).save(histCaptor.capture());
        TicketHistory history=histCaptor.getValue();
        assertThat(history.ticketId()).isEqualTo(100L);
        assertThat(history.action()).isEqualTo(TicketHistoryAction.CREATED);
        assertThat(history.oldStatus()).isNull();
        assertThat(history.newStatus()).isEqualTo(TicketStatus.OPEN);
        assertThat(history.changedByUserId()).isEqualTo(42L);
        assertThat(history.changedAt()).isNotNull();

        ArgumentCaptor<TicketCreatedEvent> eCaptor=ArgumentCaptor.forClass(TicketCreatedEvent.class);
        verify(f.eventPublisher,times(1)).publish(eCaptor.capture());
        TicketCreatedEvent evt=eCaptor.getValue();

        assertThat(evt.ticketId()).isEqualTo(100L);
        assertThat(evt.createdByUserId()).isEqualTo(42L);
        assertThat(evt.createdAt()).isNotNull();
    }

 static void changeStatus_userCannotChangeOthers_throwsForbidden(){
        Fixture f=new Fixture();
        Ticket existing = new Ticket(10L,"t","d",TicketStatus.OPEN, Instant.now(),999L);
        when(f.ticketRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(f.currentUser.currentUserId()).thenReturn(42L);
        when(f.currentUser.currentUserRole()).thenReturn(Role.USER);

        assertThatThrownBy(()->f.service.changeStatus(10L,TicketStatus.IN_PROGRESS))
            .isInstanceOf(TicketService.ForbiddenException.class)
            .hasMessageContaining("only update your own tickets");

        verify(f.ticketRepository,never()).save(any());
        verify(f.ticketHistoryRepository,never()).save(any());
 }
 static void changeStatus_userCanChange(){
        Fixture f=new Fixture();
        Ticket existing =new Ticket(10L,"t","d",TicketStatus.OPEN,Instant.now(),42L);
        when(f.ticketRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(f.currentUser.currentUserId()).thenReturn(42L);
        when(f.currentUser.currentUserRole()).thenReturn(Role.USER);

        when(f.ticketRepository.save(any(Ticket.class))).thenAnswer(inv->inv.getArgument(0));

        Ticket saved=f.service.changeStatus(10L,TicketStatus.IN_PROGRESS);

        assertThat(saved.id()).isEqualTo(10L);
        assertThat(saved.status()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(saved.createdByUserId()).isEqualTo(42L);

        ArgumentCaptor<Ticket> captor=ArgumentCaptor.forClass(Ticket.class);
        verify(f.ticketRepository).save(captor.capture());
        Ticket updatedSentToRepo=captor.getValue();

        assertThat(updatedSentToRepo.id()).isEqualTo(10L);
        assertThat(updatedSentToRepo.status()).isEqualTo(TicketStatus.IN_PROGRESS);

        //TicketHistory
     ArgumentCaptor<TicketHistory> hcaptor=ArgumentCaptor.forClass(TicketHistory.class);
     verify(f.ticketHistoryRepository).save(hcaptor.capture());
     TicketHistory history=hcaptor.getValue();

     assertThat(history.ticketId()).isEqualTo(10L);
     assertThat(history.action()).isEqualTo(TicketHistoryAction.STATUS_CHANGED);
     assertThat(history.oldStatus()).isEqualTo(TicketStatus.OPEN);
     assertThat(history.newStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
     assertThat(history.changedByUserId()).isEqualTo(42L);
     assertThat(history.changedAt()).isNotNull();

 }
 static void changeStatus_adminCanChangeany(){
        Fixture f=new Fixture();

        Ticket existing=new Ticket(10L,"t","d",TicketStatus.OPEN,Instant.now(),42L);
        when(f.currentUser.currentUserId()).thenReturn(100L);
        when(f.ticketRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(f.currentUser.currentUserRole()).thenReturn(Role.ADMIN);

        when(f.ticketRepository.save(any(Ticket.class))).thenAnswer(inv->inv.getArgument(0));

        Ticket saved=f.service.changeStatus(10L,TicketStatus.IN_PROGRESS);

     assertThat(saved.id()).isEqualTo(10L);
     assertThat(saved.status()).isEqualTo(TicketStatus.IN_PROGRESS);
     assertThat(saved.createdByUserId()).isEqualTo(42L);

     ArgumentCaptor<Ticket> captor=ArgumentCaptor.forClass(Ticket.class);
     verify(f.ticketRepository).save(captor.capture());
     Ticket updatedSentToRepo=captor.getValue();
     assertThat(updatedSentToRepo.id()).isEqualTo(10L);
     assertThat(updatedSentToRepo.status()).isEqualTo(TicketStatus.IN_PROGRESS);

     ArgumentCaptor<TicketHistory> hcaptor=ArgumentCaptor.forClass(TicketHistory.class);
     verify(f.ticketHistoryRepository).save(hcaptor.capture());
     TicketHistory history=hcaptor.getValue();

     assertThat(history.ticketId()).isEqualTo(10L);
     assertThat(history.action()).isEqualTo(TicketHistoryAction.STATUS_CHANGED);
     assertThat(history.oldStatus()).isEqualTo(TicketStatus.OPEN);
     assertThat(history.newStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
     assertThat(history.changedByUserId()).isEqualTo(100L);
     assertThat(history.changedAt()).isNotNull();
 }
    static void changeStatus_ticketNotFound_throws_andDoesNotSaveAnything() {
        Fixture f = new Fixture();

        when(f.ticketRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> f.service.changeStatus(10L, TicketStatus.IN_PROGRESS))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Ticket not found");

        verify(f.ticketRepository, never()).save(any());
        verify(f.ticketHistoryRepository, never()).save(any());
    }
}
