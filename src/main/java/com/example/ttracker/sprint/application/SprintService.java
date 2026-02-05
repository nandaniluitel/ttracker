package com.example.ttracker.sprint.application;

import com.example.ttracker.security.application.port.out.CurrentUserPort;
import com.example.ttracker.security.domain.model.Sprint;
import com.example.ttracker.sprint.adapter.out.persistence.SprintRepositoryPort;
import com.example.ttracker.sprint.domain.CreateSprintCommand;
import com.example.ttracker.sprint.domain.UpdateSprintCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.apache.logging.log4j.util.Strings.trimToNull;

@Service
public class SprintService implements SprintUseCases {

    private static final String BACKLOG_TITLE = "Backlog";

    private final CurrentUserPort currentUser;
    private final SprintRepositoryPort sprintRepository;

    public SprintService(CurrentUserPort currentUser, SprintRepositoryPort sprintRepository) {
        this.currentUser = currentUser;
        this.sprintRepository = sprintRepository;
    }

    @Override
    public Sprint create(CreateSprintCommand command) {
        var title = requireNonBlank(command.title(), "title");

        if (BACKLOG_TITLE.equalsIgnoreCase(title)) {
            throw new ConflictException("Backlog sprint is system-defined");
        }

        var goal = command.goal() == null ? null : trimToNull(command.goal());
        validateDates(command.startDate(), command.endDate());

        var sprint = new Sprint(
                null,
                title,
                goal,
                command.startDate(),
                command.endDate(),
                command.status(),
                currentUser.currentUserId(),
                null,
                Instant.now(),
                null
        );

        return sprintRepository.save(sprint);
    }


    @Override
    public Sprint getById(Long id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new DoesnotExistException("Sprint not found: " + id));
    }

    @Override
    public List<Sprint> list() {
        return sprintRepository.findAll();
    }

    @Override
    public Sprint update(Long sprintId, UpdateSprintCommand command) {
        var existing = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new DoesnotExistException("Sprint not found: " + sprintId));

        if (isBacklog(existing)
                && command.title() != null
                && !BACKLOG_TITLE.equalsIgnoreCase(command.title().trim())) {
            throw new ConflictException("Backlog sprint title cannot be changed");
        }

        var newTitle = command.title() == null ? existing.title() : requireNonBlank(command.title(), "title");
        if (!isBacklog(existing) && BACKLOG_TITLE.equalsIgnoreCase(newTitle)) {
            throw new ConflictException("Cannot rename a sprint to Backlog");
        }

        var newGoal = command.goal() == null ? existing.goal() : trimToNull(command.goal());
        var newStart = command.startDate() == null ? existing.startDate() : command.startDate();
        var newEnd = command.endDate() == null ? existing.endDate() : command.endDate();

        validateDates(newStart, newEnd);

        var updated = new Sprint(
                existing.id(),
                newTitle,
                newGoal,
                newStart,
                newEnd,
                command.status() == null ? existing.status() : command.status(),
                existing.createdByUserId(),
                currentUser.currentUserId(),
                existing.createdAt(),
                Instant.now()
        );

        return sprintRepository.save(updated);
    }

    @Transactional
    @Override
    public void delete(Long sprintId) {
        var sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new DoesnotExistException("Sprint not found: " + sprintId));

        if (isBacklog(sprint)) {
            throw new ConflictException("Backlog cannot be deleted");
        }

        // Option A: reject if tickets exist
        if (sprintRepository.hasTickets(sprintId)) {
            throw new ConflictException("Sprint has tickets; move them to Backlog before deleting");
        }

        sprintRepository.deleteById(sprintId);
    }

    @Override
    public Long getBacklogSprintId() {
        return sprintRepository.findByTitle("Backlog").map(Sprint::id).orElseThrow(() -> new IllegalStateException("Backlog sprint missing"));
    }

    private boolean isBacklog(Sprint sprint) {
        return sprint.title() != null && BACKLOG_TITLE.equalsIgnoreCase(sprint.title());
    }

    private static void validateDates(LocalDate start, LocalDate end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("startDate must be <= endDate");
        }
    }

    private static String requireNonBlank(String s, String field) {
        if (s == null || s.trim().isEmpty()) {
            throw new ValidationException(field + " must not be blank");
        }
        return s.trim();
    }

    // --- Exceptions mapped by ControllerAdvice ---

    public static class DoesnotExistException extends RuntimeException {
        public DoesnotExistException(String message) {
            super(message);
        }
    }

    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) {
            super(message);
        }
    }

    public static class ValidationException extends RuntimeException {
        public ValidationException(String message) {
            super(message);
        }
    }
}
