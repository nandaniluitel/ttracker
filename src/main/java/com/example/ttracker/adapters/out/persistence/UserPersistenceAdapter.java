package com.example.ttracker.adapters.out.persistence;

import com.example.ttracker.adapters.out.persistence.entity.UserEntity;
import com.example.ttracker.adapters.out.persistence.repo.JpaUserRepository;
import com.example.ttracker.application.port.out.UserRepositoryPort;
import com.example.ttracker.domain.model.User;

import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {
    private final JpaUserRepository jpa;

    public UserPersistenceAdapter(JpaUserRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(User user) {
        UserEntity entity = UserEntity.from(user);
        jpa.save(entity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }
}
