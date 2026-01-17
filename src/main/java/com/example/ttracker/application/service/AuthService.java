package com.example.ttracker.application.service;

import com.example.ttracker.application.port.in.AuthResponse;
import com.example.ttracker.application.port.in.AuthUseCases;
import com.example.ttracker.application.port.in.LoginCommand;
import com.example.ttracker.application.port.in.RegisterCommand;
import com.example.ttracker.application.port.out.PasswordHashPort;
import com.example.ttracker.application.port.out.TokenPort;
import com.example.ttracker.application.port.out.UserRepositoryPort;

import com.example.ttracker.domain.model.Role;
import com.example.ttracker.domain.model.User;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService implements AuthUseCases {
    private final UserRepositoryPort userRepository;
    private final PasswordHashPort passwordHash;
    private final TokenPort tokenPort;

    public AuthService(UserRepositoryPort userRepository, PasswordHashPort passwordHash, TokenPort tokenPort) {
        this.userRepository = userRepository;
        this.passwordHash = passwordHash;
        this.tokenPort = tokenPort;
    }

    @Override
    public User register(RegisterCommand command) {
      String email=command.email().trim().toLowerCase();

      userRepository.findByEmail(email).ifPresent(u->{
          throw new IllegalArgumentException("Email already registered");
      });
      Role role=Role.USER;

      User userToSave = new User(null,email,passwordHash.hash(command.password()),role, Instant.now());
      return userRepository.save(userToSave);

    }


    @Override
    public AuthResponse login(LoginCommand loginCommand) {
        String email = loginCommand.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
            .orElseThrow(()->new IllegalArgumentException("Invalid credentials"));

        if(!passwordHash.matches(loginCommand.password(),user.passwordHash())){
            throw new IllegalArgumentException("Invalid credentials");
        }
        String token = tokenPort.generateToken(user.id(),user.email(),user.role());
        return new AuthResponse(token);
    }
}
