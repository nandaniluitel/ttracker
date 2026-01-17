package com.example.ttracker.application.port.in;

import com.example.ttracker.domain.model.User;

public interface AuthUseCases {
    User register(RegisterCommand command);
    AuthResponse login(LoginCommand loginCommand);

}
