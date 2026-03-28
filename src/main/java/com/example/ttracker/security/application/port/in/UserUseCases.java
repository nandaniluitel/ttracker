package com.example.ttracker.security.application.port.in;

import com.example.ttracker.security.domain.model.Role;
import com.example.ttracker.security.domain.model.User;
import java.util.List;

public interface UserUseCases {
    List<User> getAllUsers();
    User changeRole(Long id, Role role);
}
