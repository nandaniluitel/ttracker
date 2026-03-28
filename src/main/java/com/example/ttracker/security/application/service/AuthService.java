package com.example.ttracker.security.application.service;

import com.example.ttracker.security.application.port.in.AuthResponse;
import com.example.ttracker.security.application.port.in.AuthUseCases;
import com.example.ttracker.security.application.port.in.LoginCommand;
import com.example.ttracker.security.application.port.in.RegisterCommand;
import com.example.ttracker.security.application.port.out.PasswordHashPort;
import com.example.ttracker.security.application.port.out.TokenPort;
import com.example.ttracker.security.application.port.out.UserRepositoryPort;
import com.example.ttracker.security.domain.model.Role;
import com.example.ttracker.security.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService implements AuthUseCases {
    private static final Logger log= LoggerFactory.getLogger(AuthService.class);
    private final UserRepositoryPort userRepository;
    private final PasswordHashPort passwordHash;
    private final TokenPort tokenPort;

    public AuthService(UserRepositoryPort userRepository, PasswordHashPort passwordHash, TokenPort tokenPort) {
        this.userRepository = userRepository;
        this.passwordHash = passwordHash;
        this.tokenPort = tokenPort;
    }

    @Override
    public User register(RegisterCommand command) {
        String email = command.email().trim().toLowerCase();
        log.info("[Auth] Register attempt for email={}",email);


        userRepository.findByEmail(email).ifPresent(u -> {
            log.warn("[Auth] Registration failed — email already registered: email={}", email);
            throw new IllegalArgumentException("Email already registered");
        });
        String name = command.name();
        Role role = Role.USER;

        User userToSave = new User(null,name,null, email, passwordHash.hash(command.password()), role, Instant.now());
        User saved = userRepository.save(userToSave);
        log.info("[Auth] Registration successful for email={}, userId={}", email, saved.id());
        return saved;

    }


    @Override
    public AuthResponse login(LoginCommand loginCommand) {
        String email = loginCommand.email().trim().toLowerCase();
        log.info("[Auth] Login attempt for email={}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                {
                    log.warn("[Auth] Login failed — email not found: email={}", email);
                    return new IllegalArgumentException("Invalid credentials");
                });

        if (!passwordHash.matches(loginCommand.password(), user.passwordHash())) {
            log.warn("[Auth] Login failed — wrong password for email={}", email);
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = tokenPort.generateToken(user.id(), user.email(), user.role());
        log.info("[Auth] Login successful for email={}, userId={}, role={}", email, user.id(), user.role());
        return new AuthResponse(token);
    }
    // ── Validation helpers ────────────────────────────────────────────────────

    private void validateLoginCommand(LoginCommand command, String normalizedEmail) {
        if (normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!normalizedEmail.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (command.password() == null || command.password().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
    }

    private void validateRegisterCommand(RegisterCommand command, String normalizedEmail) {
        if (command.name() == null || command.name().trim().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!normalizedEmail.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (command.password() == null || command.password().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
}

