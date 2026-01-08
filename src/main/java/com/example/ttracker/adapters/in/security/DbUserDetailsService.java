package com.example.ttracker.adapters.in.security;

import com.example.ttracker.application.port.out.UserRepositoryPort;
import com.example.ttracker.domain.model.User;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class DbUserDetailsService implements UserDetailsService {
    private final UserRepositoryPort users;

    public DbUserDetailsService(UserRepositoryPort users) {
        this.users = users;
    }

    @Override public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=users.findByEmail(username.trim().toLowerCase())
            .orElseThrow(()->new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
            user.email(),
            user.passwordHash(),
            List.of(new SimpleGrantedAuthority("ROLE_"+user.role().name()))
        );
    }
}
