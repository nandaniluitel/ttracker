package com.example.ttracker.adapters.out.persistence;

import com.example.ttracker.adapters.out.persistence.entity.SprintEntity;
import com.example.ttracker.adapters.out.persistence.repo.JpaSprintRepository;
import com.example.ttracker.application.port.out.SprintRepositoryPort;
import com.example.ttracker.application.port.out.TicketRepositoryPort;
import com.example.ttracker.domain.model.Sprint;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SprintPersistenceAdapter implements SprintRepositoryPort {
    private final JpaSprintRepository jpaSprintRepository;
    private final TicketRepositoryPort ticketRepositoryPort;

    public SprintPersistenceAdapter(JpaSprintRepository jpaSprintRepository, TicketRepositoryPort ticketRepositoryPort) {
        this.jpaSprintRepository = jpaSprintRepository;
        this.ticketRepositoryPort = ticketRepositoryPort;
    }

    @Override public Sprint save(Sprint sprint) {
        var saved= jpaSprintRepository.save(SprintEntity.fromDomain(sprint));
        return saved.toDomain();
    }

    @Override public Optional<Sprint> findById(Long id) {
        return jpaSprintRepository.findById(id).map(SprintEntity::toDomain);
    }

    @Override public List<Sprint> findAll() {
        return jpaSprintRepository.findAll().stream().map(SprintEntity::toDomain).toList();
    }

    @Override public Optional<Sprint> findByTitle(String title) {
        return jpaSprintRepository.findByTitle(title).map(SprintEntity::toDomain);
    }

    @Override public Long findBacklogSprintId() {
        return jpaSprintRepository.findByTitle("Backlog").map(SprintEntity::getId).orElseThrow(()->new IllegalStateException("Backlog sprint missing"));
    }

    @Override public boolean existsById(Long id) {
        return jpaSprintRepository.existsById(id);
    }

    @Override public void deleteById(Long id) {
         jpaSprintRepository.deleteById(id);

    }

    @Override public boolean hasTickets(Long sprintId) {
        return ticketRepositoryPort.existsBySprintId(sprintId);

    }
}
