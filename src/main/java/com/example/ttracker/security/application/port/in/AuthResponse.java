package com.example.ttracker.security.application.port.in;

public record AuthResponse(String token) {
    public AuthResponse{
        if(token == null || token.isBlank()){
            throw new NullPointerException("No JWT token was made for this user");
        }
    }

}
