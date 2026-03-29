package com.example.ttracker.security.adapters.in.web;

import com.example.ttracker.security.adapters.in.jwt.AuthPrincipal;
import com.example.ttracker.security.application.port.out.UserRepositoryPort;
import com.example.ttracker.security.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepositoryPort userRepository;

    public ProfileController(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal AuthPrincipal principal) {
        User user = userRepository.findById(principal.userId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return ResponseEntity.ok(new ProfileResponse(
            user.id(),
            user.name(),
            user.email(),
            user.role().name(),
            user.profileImageUrl()
        ));
    }

    public record ProfileResponse(
        Long id,
        String name,
        String email,
        String role,
        String profileImageUrl
    ) {}
}