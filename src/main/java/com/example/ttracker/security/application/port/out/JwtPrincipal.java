package com.example.ttracker.security.application.port.out;

import com.example.ttracker.security.domain.model.Role;

public record JwtPrincipal(Long userId, String email, Role role) {
}
