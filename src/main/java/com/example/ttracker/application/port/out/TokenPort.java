package com.example.ttracker.application.port.out;

import com.example.ttracker.domain.model.Role;

public interface TokenPort {
    String generateToken(Long userId, String email, Role role);

    Long extractUserId(String token);

    String extractEmail(String token);

    Role extractRole(String token);
}
