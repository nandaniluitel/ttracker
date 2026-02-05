package com.example.ttracker.sprint.adapter.out.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSprintRepository extends JpaRepository<SprintEntity,Long>{
    Optional<SprintEntity> findByTitle(String title);
    boolean existsByTitle(String title);
}
