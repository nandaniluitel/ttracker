package com.example.ttracker.security.adapters.out.persistence.entity;

import com.example.ttracker.security.domain.model.Role;
import com.example.ttracker.security.domain.model.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@Column(name = "name", nullable = false)
    //private String name;
    //@Column(name = "profile_image_url")
    //private String profileImageUrl;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(nullable = false)
    private String role;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public UserEntity(String email, String passwordHash, String role, Instant createdAt) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    public UserEntity() {
    }

    public static UserEntity from(User user) {
        return new UserEntity(user.email(), user.passwordHash(), user.role().name(), user.createdAt());
    }

    public static User toDomain(UserEntity userEntity) {
        return new User(userEntity.getId(), userEntity.getEmail(), userEntity.getPasswordHash(), Role.valueOf(userEntity.getRole().toString()), userEntity.createdAt);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
