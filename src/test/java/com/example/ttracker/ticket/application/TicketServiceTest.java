package com.example.ttracker.ticket.application;

import com.example.ttracker.security.application.port.out.CurrentUserPort;
import com.example.ttracker.security.domain.event.TicketCreatedEvent;
import com.example.ttracker.security.domain.model.*;
import com.example.ttracker.sprint.application.SprintUseCases;
import com.example.ttracker.ticket.adapter.out.events.EventPublisherPort;
import com.example.ttracker.ticket.adapter.out.persistence.TicketHistoryRepositoryPort;
import com.example.ttracker.ticket.adapter.out.persistence.TicketRepositoryPort;
import com.example.ttracker.ticket.domain.CreateTicketCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TicketServiceTest {

    private TicketRepositoryPort ticketRepository;
    private TicketHistoryRepositoryPort ticketHistoryRepository;
    private CurrentUserPort currentUser;
    private EventPublisherPort eventPublisher;
    private SprintUseCases sprintUseCases;

    private TicketService service;

    @BeforeEach
    void setUp() {
        ticketRepository = mock(TicketRepositoryPort.class);
        ticketHistoryRepository = mock(TicketHistoryRepositoryPort.class);
        currentUser = mock(CurrentUserPort.class);
        eventPublisher = mock(EventPublisherPort.class);
        sprintUseCases = mock(SprintUseCases.class);

        service = new TicketService(
            ticketRepository,
            ticketHistoryRepository,
            currentUser,
            eventPublisher,
            sprintUseCases
        );
    }

    @Test
    void create_whenSprintIdNull_usesBacklogSprintId_savesHistory_publishesEvent() {
        when(currentUser.currentUserId()).thenReturn(42L);
        when(sprintUseCases.getBacklogSprintId()).thenReturn(1L);

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            return new Ticket(
                100L,
                t.title(),
                t.description(),
                t.status(),
                t.priority(),
                t.storyPoints(),
                t.assigneeUserId(),
                t.epicId(),
                t.sprintId(),
                t.createdByUserId(),
                t.editedByUserId(),
                t.createdAt(),
                t.updatedAt()
            );
        });

        // sprintId = null => should use backlog id
        CreateTicketCommand cmd = new CreateTicketCommand(
            " Ticket test 1 ",
            " Desc ",
            Priority.LOW,
            3,
            2L,
            1L,
            null
        );

        Ticket saved = service.create(cmd);

        assertThat(saved.id()).isEqualTo(100L);
        assertThat(saved.title()).isEqualTo("Ticket test 1");
        assertThat(saved.description()).isEqualTo("Desc");
        assertThat(saved.status()).isEqualTo(TicketStatus.BACKLOG);
        assertThat(saved.sprintId()).isEqualTo(1L);

        // verify sprintUseCases called
        verify(sprintUseCases).getBacklogSprintId();
        verify(sprintUseCases, never()).existById(anyLong());

        // verify ticket saved with backlog sprint id
        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(ticketCaptor.capture());
        Ticket toSave = ticketCaptor.getValue();
        assertThat(toSave.sprintId()).isEqualTo(1L);

        // verify history saved
        verify(ticketHistoryRepository).save(any(TicketHistory.class));

        // verify event published
        ArgumentCaptor<TicketCreatedEvent> evtCaptor = ArgumentCaptor.forClass(TicketCreatedEvent.class);
        verify(eventPublisher).publish(evtCaptor.capture());
        TicketCreatedEvent evt = evtCaptor.getValue();

        assertThat(evt.ticketId()).isEqualTo(100L);
        assertThat(evt.createdByUserId()).isEqualTo(42L); // NOTE: your event field name differs from earlier
        assertThat(evt.createdAt()).isNotNull();
    }

    @Test
    void create_whenSprintIdProvidedButNotFound_throwsIllegalArgumentException() {
        when(currentUser.currentUserId()).thenReturn(42L);
        when(sprintUseCases.existById(99L)).thenReturn(false);

        CreateTicketCommand cmd = new CreateTicketCommand(
            "T",
            "D",
            Priority.LOW,
            1,
            null,
            null,
            99L
        );

        assertThatThrownBy(() -> service.create(cmd))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Sprint not found: 99");

        verify(ticketRepository, never()).save(any());
        verify(ticketHistoryRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void create_whenSprintIdProvidedAndExists_usesThatSprint() {
        when(currentUser.currentUserId()).thenReturn(42L);
        when(sprintUseCases.existById(10L)).thenReturn(true);

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> {
            Ticket t = inv.getArgument(0);
            return new Ticket(
                100L,
                t.title(),
                t.description(),
                t.status(),
                t.priority(),
                t.storyPoints(),
                t.assigneeUserId(),
                t.epicId(),
                t.sprintId(),
                t.createdByUserId(),
                t.editedByUserId(),
                t.createdAt(),
                t.updatedAt()
            );
        });

        CreateTicketCommand cmd = new CreateTicketCommand(
            "T",
            "D",
            Priority.HIGH,
            2,
            null,
            null,
            10L
        );

        Ticket saved = service.create(cmd);
        assertThat(saved.sprintId()).isEqualTo(10L);

        verify(sprintUseCases).existById(10L);
        verify(sprintUseCases, never()).getBacklogSprintId();
    }

    @Test
    void create_storyPointsLessThanZero_throws() {
        when(currentUser.currentUserId()).thenReturn(42L);

        CreateTicketCommand cmd = new CreateTicketCommand(
            "T", "D", Priority.LOW, -1, null, null, null
        );

        assertThatThrownBy(() -> service.create(cmd))
            .isInstanceOf(TicketService.LessThanZeroException.class);

        verifyNoInteractions(ticketRepository, ticketHistoryRepository, eventPublisher, sprintUseCases);
    }

    @Test
    void changeStatus_ticketNotFound_throwsNotFoundException() {
        when(ticketRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeStatus(10L, TicketStatus.IN_PROGRESS))
            .isInstanceOf(TicketService.NotFoundException.class)
            .hasMessageContaining("Ticket not found: 10");

        verify(ticketRepository, never()).save(any());
        verify(ticketHistoryRepository, never()).save(any());
    }

    @Test
    void changeStatus_userCannotEditOthersTicket_throwsForbidden() {
        Ticket existing = new Ticket(
            10L, "t", "d",
            TicketStatus.BACKLOG, Priority.LOW, 1,
            null, null, 1L,
            999L, null,
            Instant.now(), null
        );

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(currentUser.currentUserId()).thenReturn(42L);
        when(currentUser.currentUserRole()).thenReturn(Role.USER);

        assertThatThrownBy(() -> service.changeStatus(10L, TicketStatus.IN_PROGRESS))
            .isInstanceOf(TicketService.ForbiddenException.class)
            .hasMessageContaining("own ticket");

        verify(ticketRepository, never()).save(any());
        verify(ticketHistoryRepository, never()).save(any());
    }

    @Test
    void changeStatus_userCannotReopenDoneTicket_throwsForbidden() {
        Ticket existing = new Ticket(
            10L, "t", "d",
            TicketStatus.DONE, Priority.LOW, 1,
            null, null, 1L,
            42L, null,
            Instant.now(), null
        );

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(currentUser.currentUserId()).thenReturn(42L);
        when(currentUser.currentUserRole()).thenReturn(Role.USER);

        assertThatThrownBy(() -> service.changeStatus(10L, TicketStatus.IN_PROGRESS))
            .isInstanceOf(TicketService.ForbiddenException.class)
            .hasMessageContaining("reopen DONE");

        verify(ticketRepository, never()).save(any());
        verify(ticketHistoryRepository, never()).save(any());
    }

    @Test
    void changeStatus_adminCanEditAnyTicket_savesAndWritesHistory() {
        Ticket existing = new Ticket(
            10L, "t", "d",
            TicketStatus.BACKLOG, Priority.LOW, 1,
            null, null, 1L,
            42L, null,
            Instant.now(), null
        );

        when(ticketRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(currentUser.currentUserId()).thenReturn(100L);
        when(currentUser.currentUserRole()).thenReturn(Role.ADMIN);
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket saved = service.changeStatus(10L, TicketStatus.IN_PROGRESS);

        assertThat(saved.status()).isEqualTo(TicketStatus.IN_PROGRESS);

        verify(ticketRepository).save(any(Ticket.class));
        verify(ticketHistoryRepository).save(any(TicketHistory.class));
    }
}
