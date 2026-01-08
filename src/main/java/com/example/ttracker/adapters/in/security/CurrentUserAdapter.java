package com.example.ttracker.adapters.in.security;

import com.example.ttracker.application.port.out.CurrentUserPort;
import com.example.ttracker.application.port.out.UserRepositoryPort;
import com.example.ttracker.domain.model.Role;
import com.example.ttracker.domain.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserAdapter implements CurrentUserPort {
    private final UserRepositoryPort users;

    public CurrentUserAdapter(UserRepositoryPort users) {
        this.users = users;
    }

    @Override public Long currentUserId() {
        return currentUser().id();
    }

    @Override public Role currentUserRole() {
        return currentUser().role();
    }

    @Override public String CurrentUserEmail() {
        return currentUser().email();
    }
    private User currentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth==null || auth.getName()==null){
            throw new IllegalStateException("No authenticated user");
        }
        String email= auth.getName().trim().toLowerCase();
        return users.findByEmail(email).orElseThrow(()->new IllegalStateException("User not found in DB"));
    }
}
