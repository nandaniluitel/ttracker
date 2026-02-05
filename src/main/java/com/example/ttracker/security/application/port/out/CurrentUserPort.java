package com.example.ttracker.security.application.port.out;

import com.example.ttracker.security.domain.model.Role;

public interface CurrentUserPort {
    Long currentUserId();

    Role currentUserRole();

    String CurrentUserEmail();
}
