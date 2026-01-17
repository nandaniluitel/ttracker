package com.example.ttracker.application.port.out;

import com.example.ttracker.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);
}
