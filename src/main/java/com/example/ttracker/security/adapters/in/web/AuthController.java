package com.example.ttracker.security.adapters.in.web;

import com.example.ttracker.security.application.port.in.AuthResponse;
import com.example.ttracker.security.application.port.in.AuthUseCases;
import com.example.ttracker.security.application.port.in.LoginCommand;
import com.example.ttracker.security.application.port.in.RegisterCommand;
import com.example.ttracker.security.domain.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthUseCases auth;


    public AuthController(AuthUseCases auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest req) {
        User u = auth.register(new RegisterCommand(req.email, req.password()));
        return ResponseEntity.ok(new RegisterResponse(u.id(), u.email(), u.role().name()));
    }

    ;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse token = auth.login(new LoginCommand(req.email(), req.password()));
        return ResponseEntity.ok(token);
    }

    ;

    public record RegisterRequest(@Email @NotBlank String email, @NotBlank String password) {
    }

    public record RegisterResponse(Long id, String email, String role) {
    }

    ;

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
    }
}



