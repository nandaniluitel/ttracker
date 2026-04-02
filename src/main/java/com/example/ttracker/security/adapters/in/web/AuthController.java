package com.example.ttracker.security.adapters.in.web;

import com.example.ttracker.security.application.port.in.AuthResponse;
import com.example.ttracker.security.application.port.in.AuthUseCases;
import com.example.ttracker.security.application.port.in.LoginCommand;
import com.example.ttracker.security.application.port.in.RegisterCommand;
import com.example.ttracker.security.application.service.BlacklistService;
import com.example.ttracker.security.domain.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthUseCases auth;
    private final BlacklistService blacklistService;

    public AuthController(AuthUseCases auth, BlacklistService blacklistService) {
        this.auth = auth;
        this.blacklistService = blacklistService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        log.info("[AuthController] Register request for email={}", req.email());

        User u = auth.register(new RegisterCommand(req.name(), req.email(), req.password()));

        log.info("[AuthController] Register successful for email={}, userId={}", u.email(), u.id());
        return ResponseEntity.ok(new RegisterResponse(u.id(), u.name(), u.email(), u.role().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        log.info("[AuthController] Login request for email={}", req.email());

        AuthResponse token = auth.login(new LoginCommand(req.email(), req.password()));

        log.info("[AuthController] Login successful for email={}", req.email());
        return ResponseEntity.ok(token);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.replace("Bearer ", "");
            try {
                blacklistService.blacklist(token);
            } catch (Exception e) {
                // token already expired, nothing to blacklist
            }
        }
        return ResponseEntity.ok().build();
    }

    // ── Request / Response records ────────────────────────────────────────────

    public record RegisterRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
    ) {}

    public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
    ) {}

    public record RegisterResponse(Long id, String name, String email, String role) {}
}



