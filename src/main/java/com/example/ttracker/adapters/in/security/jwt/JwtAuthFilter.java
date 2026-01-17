package com.example.ttracker.adapters.in.security.jwt;

import com.example.ttracker.application.port.out.TokenPort;
import com.example.ttracker.domain.model.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.springframework.boot.autoconfigure.AutoConfigurations.of;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final TokenPort tokenPort;

    public JwtAuthFilter(TokenPort tokenPort) {
        this.tokenPort = tokenPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String auth=request.getHeader("Authorization");
        if(auth==null || !auth.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;
        }
        String token = auth.substring(7);
        try{
            Long userId= tokenPort.extractUserId(token);
            String email=tokenPort.extractEmail(token);
            Role role=tokenPort.extractRole(token);

            var principal = new AuthPrincipal(userId,email,role);
            var authorities=(role==null)
                    ?List.<SimpleGrantedAuthority>of()
                    :List.of(new SimpleGrantedAuthority("ROLE"+role.name()));
            var authentication=new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        authorities
            );
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        }

    }
}
