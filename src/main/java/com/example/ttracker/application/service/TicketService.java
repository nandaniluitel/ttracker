package com.example.ttracker.application.service;

import com.example.ttracker.application.port.in.CreateTicketCommand;
import com.example.ttracker.application.port.in.TicketUseCases;
import com.example.ttracker.application.port.out.CurrentUserPort;
import com.example.ttracker.application.port.out.EventPublisherPort;
import com.example.ttracker.application.port.out.TicketHistoryRepositoryPort;
import com.example.ttracker.application.port.out.TicketRepositoryPort;
import com.example.ttracker.domain.event.TicketCreatedEvent;
import com.example.ttracker.domain.model.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
@Service
public class TicketService implements TicketUseCases {

    private final TicketRepositoryPort ticketRepository;
    private final TicketHistoryRepositoryPort ticketHistoryRepository;
    private final CurrentUserPort currentUser;
    private final EventPublisherPort eventPublisher;

    public TicketService(TicketRepositoryPort ticketRepository, TicketHistoryRepositoryPort ticketHistoryRepository, CurrentUserPort currentUser, EventPublisherPort eventPublisher) {
        this.ticketRepository = ticketRepository;
        this.ticketHistoryRepository = ticketHistoryRepository;
        this.currentUser = currentUser;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    @Override
    public Ticket create(CreateTicketCommand command) {
        String title = command.title().trim();
        String description= command.description().trim();

        Long userId=currentUser.currentUserId();

        Ticket ticketToSave=new Ticket(
                null,
                title,
                description,
                TicketStatus.OPEN,
                Instant.now(),
                userId

        );

        //SAVE #1
        Ticket savedTicket = ticketRepository.save(ticketToSave);

        //rollback demo
        if(title.toUpperCase().contains("FAIL")){
            throw new RuntimeException("Intentional failure to demonstrate transaction rollback");
        }

        //SAVE #2
        TicketHistory ticketHistory=new TicketHistory(
                null,
                savedTicket.id(),
                TicketHistoryAction.CREATED,
                null,
                 TicketStatus.OPEN,
                Instant.now(),
                userId
        );
        ticketHistoryRepository.save(ticketHistory);

        eventPublisher.publish(new TicketCreatedEvent(savedTicket.id(),userId,Instant.now()));

        return savedTicket;
    }

    @Override
    public Ticket getById(Long id) {
        return ticketRepository.findById(id).orElseThrow(()->new NullPointerException("Ticket not found"));
    }

    @Override
    public List<Ticket> list() {
        return ticketRepository.findAll();
    }

    @Override
    @Transactional
    public Ticket changeStatus(Long ticketId, TicketStatus newStatus) {
        Ticket existing = ticketRepository.findById(ticketId)
                .orElseThrow(()->new NullPointerException("Ticket not found"));

        Role role=currentUser.currentUserRole();
        if(role == Role.USER && !existing.createdByUserId().equals(currentUser.currentUserId())){
            throw new RuntimeException("you can only update your own tickets");
        }

        Ticket updated = new Ticket(existing.id(), existing.title(),existing.description(),newStatus,existing.createdAt(),existing.createdByUserId());

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
}
