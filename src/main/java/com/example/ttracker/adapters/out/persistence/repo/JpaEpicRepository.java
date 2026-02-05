package com.example.ttracker.adapters.out.persistence.repo;

import com.example.ttracker.adapters.out.persistence.entity.EpicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEpicRepository extends JpaRepository<EpicEntity,Long> {
}
