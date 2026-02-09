package com.example.ttracker.epic.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEpicRepository extends JpaRepository<EpicEntity,Long> {
}
