package com.example.ttracker.security.adapters.in.jwt;

import com.example.ttracker.security.domain.model.Role;

public record AuthPrincipal(Long userId, String email, Role role) {

}
