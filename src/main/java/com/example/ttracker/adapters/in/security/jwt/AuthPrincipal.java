package com.example.ttracker.adapters.in.security.jwt;

import com.example.ttracker.domain.model.Role;

public record AuthPrincipal(Long userId, String email, Role role) {
}
