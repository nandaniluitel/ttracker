package com.example.ttracker.security.application.port.in;

import com.example.ttracker.security.domain.model.User;

public interface AuthUseCases {
    User register(RegisterCommand command);

    AuthResponse login(LoginCommand loginCommand);

}
