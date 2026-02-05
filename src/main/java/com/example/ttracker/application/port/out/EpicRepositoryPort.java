package com.example.ttracker.application.port.out;

import com.example.ttracker.domain.model.Epic;
import com.example.ttracker.domain.model.Ticket;
import java.util.List;
import java.util.Optional;

public interface EpicRepositoryPort {
    Epic save(Epic epic);

    Optional<Epic> findById(Long id);

    List<Epic> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
}
