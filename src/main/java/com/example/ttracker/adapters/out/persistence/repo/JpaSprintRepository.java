package com.example.ttracker.adapters.out.persistence.repo;

import com.example.ttracker.adapters.out.persistence.entity.SprintEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSprintRepository extends JpaRepository<SprintEntity,Long>{
    Optional<SprintEntity> findByTitle(String title);
    boolean existsByTitle(String title);
}
