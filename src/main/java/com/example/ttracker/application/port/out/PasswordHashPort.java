package com.example.ttracker.application.port.out;

public interface PasswordHashPort {
    String hash(String rawPassword);
    boolean matches(String rawPassword, String passwordHash);
}
