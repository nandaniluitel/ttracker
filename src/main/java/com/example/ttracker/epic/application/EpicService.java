package com.example.ttracker.epic.application;

import com.example.ttracker.epic.domain.CreateEpicCommand;
import com.example.ttracker.epic.domain.UpdateEpicCommand;
import com.example.ttracker.security.application.port.out.CurrentUserPort;
import com.example.ttracker.epic.adapter.out.persistence.EpicRepositoryPort;
import com.example.ttracker.ticket.adapter.out.persistence.TicketRepositoryPort;
import com.example.ttracker.security.domain.model.Epic;
import com.example.ttracker.security.domain.model.EpicStatus;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EpicService implements EpicUseCases {
    private final EpicRepositoryPort epicRepository;
    private final CurrentUserPort currentUser;
    private final TicketRepositoryPort ticketRepository;

    public EpicService(EpicRepositoryPort epicRepository, CurrentUserPort currentUser,
        TicketRepositoryPort ticketRepository) {
        this.epicRepository = epicRepository;
        this.currentUser = currentUser;
        this.ticketRepository = ticketRepository;
    }

    @Override public Epic create(CreateEpicCommand command) {
        String title = command.title() == null ? null : command.title().trim();
        if (title == null || title.isEmpty()) {
            throw new ValidationException("title must not be blank");
        }
        String description= command.description().trim();

        Epic tosave= new Epic(
            null,
            title,
            description,
            EpicStatus.OPEN,
            command.priority(),
            command.assigneeUserId(),
            currentUser.currentUserId(),
            null,
            Instant.now(),
            null
        );
        return epicRepository.save(tosave);

    }

    @Override public Epic getById(Long id) {
        return epicRepository.findById(id).orElseThrow( ()->new DoesnotExistException("Epic not found" + id));
    }

    @Override public List<Epic> list() {

        return epicRepository.findAll();
    }

    @Override public Epic update(Long epicId, UpdateEpicCommand cmd) {
        Epic existing =epicRepository.findById(epicId).orElseThrow( ()->new DoesnotExistException("Epic doesnt exist"));
        String newTitle = (cmd.title() == null)
            ? existing.title()
            : requireNonBlankIfProvided(cmd.title(), "title");
        Epic updated=new Epic(
            existing.id(),
            newTitle,
            cmd.description() == null ? existing.description() : cmd.description().trim(),
            cmd.status() == null ? existing.status() : cmd.status(),
            cmd.priority() == null ? existing.priority() : cmd.priority(),
            cmd.assigneeUserId(), // allowed to set null
            existing.createdByUserId(),
            currentUser.currentUserId(),
            existing.createdAt(),
            Instant.now()
        );

        return epicRepository.save(updated);
    }


    @Transactional
    @Override public void delete(Long epicId) {
        if (!epicRepository.existsById(epicId)) {
            throw new DoesnotExistException("Epic not found");
        }
        // Behavior B: unassign tickets then delete epic
        ticketRepository.clearEpicForTickets(epicId);
        epicRepository.deleteById(epicId);
    }
    private static String requireNonBlankIfProvided(String s, String field) {
        if (s == null) return null;              // means "no change"
        String trimmed = s.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException(field + " must not be blank");
        }
        return trimmed;
    }
    public static class DoesnotExistException extends NullPointerException{
        public DoesnotExistException(String message) {
            super(message);
        }
    }
    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) { super(message); }
    }

}
