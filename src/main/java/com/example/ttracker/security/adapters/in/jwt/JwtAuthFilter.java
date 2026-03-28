package com.example.ttracker.security.adapters.in.jwt;

import com.example.ttracker.security.application.port.out.TokenPort;
import com.example.ttracker.security.domain.model.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenPort tokenPort;

    public JwtAuthFilter(TokenPort tokenPort) {
        this.tokenPort = tokenPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        // Skip JWT check for OPTIONS requests
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);

        // No token -> just continue (public endpoints will work, protected ones will be rejected later)
        if (auth == null || !auth.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = auth.substring(7);

        try {
            Long userId = tokenPort.extractUserId(token);
            String email = tokenPort.extractEmail(token);
            Role role = tokenPort.extractRole(token);

            var principal = new AuthPrincipal(userId, email, role);

            var authorities = (role == null)
                ? List.<SimpleGrantedAuthority>of()
                : List.of(new SimpleGrantedAuthority("ROLE_" + role.name())); // ✅ underscore

            var authentication = new UsernamePasswordAuthenticationToken(
                principal, null, authorities
            );

            // ✅ THIS is what tells Spring Security “user is authenticated”
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // ✅ continue filter chain
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        }
    }
}
