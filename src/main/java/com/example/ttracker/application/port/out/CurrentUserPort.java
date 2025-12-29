package com.example.ttracker.application.port.out;

import com.example.ttracker.domain.model.Role;

public interface CurrentUserPort {
    Long currentUserId();
    Role currentUserRole();
    String CurrentUserEmail();
}
