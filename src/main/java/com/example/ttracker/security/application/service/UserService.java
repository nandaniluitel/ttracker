package com.example.ttracker.security.application.service;

import com.example.ttracker.security.application.port.in.UserUseCases;
import com.example.ttracker.security.application.port.out.UserRepositoryPort;
import com.example.ttracker.security.domain.model.Role;
import com.example.ttracker.security.domain.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserUseCases {

    private final UserRepositoryPort userRepository;

    public UserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User changeRole(Long id, Role role) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        User updated = new User(
            user.id(),
            user.name(),
            user.profileImageUrl(),
            user.email(),
            user.passwordHash(),
            role,
            user.createdAt()
        );
        return userRepository.save(updated);
    }
}