package com.example.ttracker.adapters.out.persistence;

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

    @Override public User save(User user) {
    User user1 =  new User(null, user.email(), user.passwordHash())
    }

    @Override public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override public Optional<User> findById(Long id) {
        return Optional.empty();
    }
}
