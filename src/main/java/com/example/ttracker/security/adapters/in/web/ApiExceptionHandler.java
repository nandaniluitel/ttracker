package com.example.ttracker.security.adapters.in.web;

import com.example.ttracker.sprint.application.SprintService;
import com.example.ttracker.ticket.application.TicketService;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validation(MethodArgumentNotValidException ex) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                fe -> fe.getField(),
                fe -> fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage(),
                (a, b) -> a
            ));

        return ResponseEntity.badRequest().body(Map.of(
            "error", "Validation failed",
            "fields", fieldErrors
        ));
    }

    //409
    @ExceptionHandler(SprintService.ConflictException.class)
    public ResponseEntity<?> sprintConflict(SprintService.ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }
    //404
    @ExceptionHandler(SprintService.ValidationException.class)
    public ResponseEntity<?> sprintValidation(SprintService.ValidationException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
    //404
    @ExceptionHandler(SprintService.DoesnotExistException.class)
    public ResponseEntity<?> sprintNotFound(SprintService.DoesnotExistException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error",ex.getMessage()));
    }
//400
    @ExceptionHandler(TicketService.ForbiddenException.class)
    public ResponseEntity<?> forbidden(TicketService.ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }
//500
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> server(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }

}
