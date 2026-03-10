package com.example.ttracker.ticket.application;

import com.example.ttracker.security.application.port.out.*;
import com.example.ttracker.security.domain.event.TicketCreatedEvent;
import com.example.ttracker.security.domain.model.*;
import com.example.ttracker.sprint.application.SprintUseCases;
import com.example.ttracker.ticket.adapter.out.events.EventPublisherPort;
import com.example.ttracker.ticket.adapter.out.persistence.TicketHistoryRepositoryPort;
import com.example.ttracker.ticket.adapter.out.persistence.TicketRepositoryPort;
import com.example.ttracker.ticket.domain.CreateTicketCommand;
import com.example.ttracker.ticket.domain.TicketFilter;
import com.example.ttracker.ticket.domain.UpdateTicketCommand;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class TicketService implements TicketUseCases{

    private final TicketRepositoryPort ticketRepository;
    private final TicketHistoryRepositoryPort ticketHistoryRepository;
    private final CurrentUserPort currentUser;
    private final EventPublisherPort eventPublisher;

    private final SprintUseCases sprintUseCases;

    public TicketService(TicketRepositoryPort ticketRepository, TicketHistoryRepositoryPort ticketHistoryRepository,
                         CurrentUserPort currentUser, EventPublisherPort eventPublisher,   @Lazy SprintUseCases sprintUseCases) {
        this.ticketRepository = ticketRepository;
        this.ticketHistoryRepository = ticketHistoryRepository;
        this.currentUser = currentUser;
        this.eventPublisher = eventPublisher;

        this.sprintUseCases = sprintUseCases;
    }

    @Transactional
    public Ticket create(CreateTicketCommand command) {
        String title = command.title().trim();
        String description = command.description().trim();

        Long userId = currentUser.currentUserId();

        Priority priority=command.priority()==null?Priority.MEDIUM:command.priority();
        Integer sp=command.storyPoints();
        if (sp!=null && sp < 0) {
            throw (new LessThanZeroException("Story points must be greater than 0"));
        }

        Long sprintId;
        if (command.sprintId() == null) {
            sprintId = sprintUseCases.getBacklogSprintId();
        } else {
            if (!sprintUseCases.existById(command.sprintId())) {
                throw new IllegalArgumentException("Sprint not found: " + command.sprintId());
            }
            sprintId = command.sprintId();
        }



        Ticket ticketToSave = new Ticket(
                null,
                title,
                description,
                TicketStatus.BACKLOG,
                priority,
                sp,
                command.assigneeUserId(),
                command.epicId(),
                sprintId,
                userId,
                null,
                Instant.now(),
                null
        );

        //SAVE #1
        Ticket savedTicket = ticketRepository.save(ticketToSave);

        //rollback demo
        if (title.toUpperCase().contains("FAIL")) {
            throw new RuntimeException("Intentional failure to demonstrate transaction rollback");
        }

        //SAVE #2
        TicketHistory ticketHistory = new TicketHistory(
                null,
                savedTicket.id(),
                TicketHistoryAction.CREATED,
                null,
                TicketStatus.BACKLOG,
                Instant.now(),
                userId
        );
        ticketHistoryRepository.save(ticketHistory);

        eventPublisher.publish(new TicketCreatedEvent(savedTicket.id(), userId, Instant.now()));
        return savedTicket;
    }


    public Ticket getById(Long id) {
        return ticketRepository.findById(id).orElseThrow(() -> new NullPointerException("Ticket not found"));
    }

    @Override public Boolean existBySprintId(Long sprintId) {
        return ticketRepository.existsBySprintId(sprintId);
    }

    @Override public List<Ticket> list() {
        return null;
    }

    public List<Ticket> list(TicketFilter filter) {

        return ticketRepository.findAll(filter);
    }

    public Ticket update(Long ticketId, UpdateTicketCommand command) {
        Ticket existing = getById(ticketId);
        assertCanEdit(existing);

        Long editedBy = currentUser.currentUserId();
        Role role = currentUser.currentUserRole();
        Long sprintId = existing.sprintId();

        if (command.sprintId() != null) {
            if (!sprintUseCases.existById(command.sprintId())) {
                throw new IllegalArgumentException("Sprint not found: " + command.sprintId());
            }
            sprintId = command.sprintId();
        }

        Ticket updated = new Ticket(
                existing.id(),
            command.title() == null ? existing.title() : command.title(),
         command.description() == null ? existing.description() : command.description(),
        existing.status(),//status cannot be changed
                command.priority() == null ? existing.priority() : command.priority(),
                command.storyPoints() == null ? existing.storyPoints() : command.storyPoints(),
                command.assigneeUserId(),//allowed null
                command.epicId(),//allowed null
                sprintId,
                existing.createdByUserId(),
                editedBy,
                existing.createdAt(),
                Instant.now()
        );

        //SAVE #1
        Ticket updatedTicket = ticketRepository.save(updated);
        return updatedTicket;
    }


    @Transactional
    public Ticket changeStatus(Long ticketId, TicketStatus newStatus) {
        Ticket existing = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found: " + ticketId));


        Role role = currentUser.currentUserRole();
        Long editedBy = currentUser.currentUserId();
        if (existing.status() == TicketStatus.DONE && newStatus != TicketStatus.DONE) {
            Role r = currentUser.currentUserRole();
            if (r != Role.SCRUM_MASTER && r != Role.ADMIN) {
                throw new ForbiddenException("Only SCRUM_MASTER/ADMIN can reopen DONE tickets");
            }
        }
        assertCanEdit(existing);


        Ticket updated =
                new Ticket(existing.id(), existing.title(), existing.description(), newStatus, existing.priority(),
                        existing.storyPoints(), existing.assigneeUserId(),
                        existing.epicId(), existing.sprintId(), existing.createdByUserId(), editedBy, existing.createdAt(),
                        Instant.now());

        Ticket saved = ticketRepository.save(updated);

        TicketHistory history = new TicketHistory(
                null,
                saved.id(),
                TicketHistoryAction.STATUS_CHANGED,
                existing.status(),
                newStatus,
                Instant.now(),
                currentUser.currentUserId()
        );
        ticketHistoryRepository.save(history);
        return saved;
    }

    @Transactional
    public void delete(Long ticketId) {
        ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NullPointerException("Ticket not found"));
        Role r = currentUser.currentUserRole();
        if (r != Role.ADMIN) {
            throw new ForbiddenException("Only ADMIN can delete the ticket");
        }
        ticketRepository.deleteById(ticketId);
    }

    private void assertCanEdit(Ticket existing) {
        Role role = currentUser.currentUserRole();
        Long uid = currentUser.currentUserId();

        boolean ownerUser = (role == Role.USER && existing.createdByUserId().equals(uid));
        boolean elevated = (role == Role.SCRUM_MASTER || role == Role.ADMIN);
        if (!ownerUser && !elevated) {
            throw new ForbiddenException("You can only update your own ticket");
        }
    }

    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException(String message) {
            super(message);
        }
    }

    public static class LessThanZeroException extends RuntimeException {
        public LessThanZeroException(String message) {
            super(message);
        }
    }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }
}
