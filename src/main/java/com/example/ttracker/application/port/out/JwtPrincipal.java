package com.example.ttracker.application.port.out;

import com.example.ttracker.domain.model.Role;

public record JwtPrincipal(Long userId, String email, Role role) {
}
