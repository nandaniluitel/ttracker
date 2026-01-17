package com.example.ttracker.adapters.in.security;

import com.example.ttracker.adapters.in.security.jwt.AuthPrincipal;
import com.example.ttracker.application.port.out.CurrentUserPort;
import com.example.ttracker.application.port.out.UserRepositoryPort;
import com.example.ttracker.domain.model.Role;
import com.example.ttracker.domain.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserAdapter implements CurrentUserPort {



    @Override public Long currentUserId() {
        AuthPrincipal p = principal();
        return p.userId();
    }

    @Override public Role currentUserRole() {
        AuthPrincipal p=principal();
        return p.role();
    }

    @Override public String CurrentUserEmail() {
        AuthPrincipal p=principal();
        return p.email();
    }
    private AuthPrincipal principal(){
        Authentication auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth==null ){
            throw new IllegalStateException("No authenticated user");
        }
        Object p=auth.getPrincipal();
        return(p instanceof AuthPrincipal ap)?ap:null;
    }
}
