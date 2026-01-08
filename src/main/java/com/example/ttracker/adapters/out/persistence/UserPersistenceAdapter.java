package com.example.ttracker.adapters.out.persistence;

import com.example.ttracker.adapters.out.persistence.entity.UserEntity;
import com.example.ttracker.adapters.out.persistence.repo.JpaUserRepository;
import com.example.ttracker.application.port.out.UserRepositoryPort;
import com.example.ttracker.domain.model.Role;
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
    public User save(User user) {
        UserEntity entity = UserEntity.from(user);
        UserEntity saved=jpa.save(entity);
        return toDomain(saved);

    }

    @Override
    public Optional<User> findByEmail(String email) {

        return jpa.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

private User toDomain(UserEntity e){
    return new User(e.getId(),e.getEmail(),e.getPasswordHash(), Role.valueOf(e.getRole()),e.getCreatedAt());
}
}