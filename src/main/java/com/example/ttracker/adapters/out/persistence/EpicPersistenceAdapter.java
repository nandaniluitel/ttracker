package com.example.ttracker.adapters.out.persistence;

import com.example.ttracker.adapters.out.persistence.entity.EpicEntity;
import com.example.ttracker.adapters.out.persistence.entity.TicketEntity;
import com.example.ttracker.adapters.out.persistence.repo.JpaEpicRepository;
import com.example.ttracker.application.port.out.EpicRepositoryPort;
import com.example.ttracker.domain.model.Epic;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class EpicPersistenceAdapter implements EpicRepositoryPort {
    private final JpaEpicRepository jpa;

    public EpicPersistenceAdapter(JpaEpicRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Epic save(Epic epic) {
         EpicEntity saved=jpa.save(EpicEntity.from(epic));
         return EpicEntity.toDomain(saved);
    }

    @Override public Optional<Epic> findById(Long id) {
       return jpa.findById(id).map(EpicEntity::toDomain);
    }

    @Override public List<Epic> findAll() {
        return jpa.findAll().stream().map(EpicEntity::toDomain).toList();
    }

    @Override public void deleteById(Long id) {
         jpa.deleteById(id);
    }

    @Override public boolean existsById(Long id) {
        return jpa.existsById(id);
    }
}
