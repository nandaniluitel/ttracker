package com.example.ttracker.security.application.port.out;

public interface PasswordHashPort {
    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
