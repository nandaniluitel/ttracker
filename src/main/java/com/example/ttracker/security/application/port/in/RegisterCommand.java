package com.example.ttracker.security.application.port.in;

public record RegisterCommand(String name,String email, String password) {
    public RegisterCommand{
        if(name==null || name.isBlank()){
            throw new IllegalArgumentException("name must not be null");
        }
        if(email==null || email.isBlank()){
            throw new IllegalArgumentException("Email must not be null");
        }
        if(password==null || password.isBlank())
        {
            throw new IllegalArgumentException("Password must not be null");
        }
    }
}
