package com.example.ttracker.application.port.in;

import com.example.ttracker.domain.model.User;

public interface AuthUseCases {
    void register(RegisterCommand command);
    void login(LoginCommand loginCommand);

}
