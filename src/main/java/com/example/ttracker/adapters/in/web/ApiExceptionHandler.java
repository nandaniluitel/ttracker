package com.example.ttracker.adapters.in.web;

import com.example.ttracker.application.service.TicketService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validation(MethodArgumentNotValidException ex){
        return ResponseEntity.badRequest().body(Map.of("error","Validation fails"));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(Map.of("error",ex.getMessage()));
    }
    @ExceptionHandler(TicketService.ForbiddenException.class)
    public ResponseEntity<?> forbidden(TicketService.ForbiddenException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",ex.getMessage()));
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> server(RuntimeException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error",ex.getMessage()));
    }

}
