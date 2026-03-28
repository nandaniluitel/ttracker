package com.example.ttracker.epic.adapter.out.persistence;

import com.example.ttracker.epic.domain.Epic;

import java.util.List;
import java.util.Optional;

public interface EpicRepositoryPort {
    Epic save(Epic epic);

    Optional<Epic> findById(Long id);

    List<Epic> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
