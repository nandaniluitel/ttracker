package com.example.ttracker.security.application.port.out;

import com.example.ttracker.security.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);
}
