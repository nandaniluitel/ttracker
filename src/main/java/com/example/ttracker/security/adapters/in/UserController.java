package com.example.ttracker.security.adapters.in.web;

import com.example.ttracker.security.application.port.in.UserUseCases;
import com.example.ttracker.security.domain.model.Role;
import com.example.ttracker.security.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserUseCases userUseCases;

    public UserController(UserUseCases userUseCases) {
        this.userUseCases = userUseCases;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userUseCases.getAllUsers()
            .stream()
            .map(UserResponse::from)
            .toList();
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> changeRole(
        @PathVariable Long id,
        @RequestBody ChangeRoleRequest req
    ) {
        User updated = userUseCases.changeRole(id, Role.valueOf(req.role()));
        return ResponseEntity.ok(UserResponse.from(updated));
    }

    public record ChangeRoleRequest(String role) {}

    public record UserResponse(
        Long id,
        String name,
        String email,
        String role,
        String profileImageUrl
    ) {
        public static UserResponse from(User user) {
            return new UserResponse(
                user.id(),
                user.name(),
                user.email(),
                user.role().name(),
                user.profileImageUrl()
            );
        }
    }
}