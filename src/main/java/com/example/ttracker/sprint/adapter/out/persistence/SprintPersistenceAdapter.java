package com.example.ttracker.sprint.adapter.out.persistence;

import com.example.ttracker.sprint.domain.Sprint;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SprintPersistenceAdapter implements SprintRepositoryPort {
    private final JpaSprintRepository jpaSprintRepository;
        ;

    public SprintPersistenceAdapter(JpaSprintRepository jpaSprintRepository) {
        this.jpaSprintRepository = jpaSprintRepository;
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

    @Override public boolean existsById(Long id) {
        return jpaSprintRepository.existsById(id);
    }

    @Override public void deleteById(Long id) {
         jpaSprintRepository.deleteById(id);

    }

}
