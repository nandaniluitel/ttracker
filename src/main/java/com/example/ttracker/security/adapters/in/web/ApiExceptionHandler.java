package com.example.ttracker.security.adapters.in.web;

import com.example.ttracker.sprint.application.SprintService;
import com.example.ttracker.ticket.application.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    // 400 — field-level validation (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validation(MethodArgumentNotValidException ex) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                fe -> fe.getField(),
                fe -> fe.getDefaultMessage() == null ? "invalid" : fe.getDefaultMessage(),
                (a, b) -> a
            ));

        log.warn("[ExceptionHandler] Validation failed: fields={}", fieldErrors);

        return ResponseEntity.badRequest().body(Map.of(
            "error", "Validation failed",
            "fields", fieldErrors
        ));
    }

    // 400 — general bad input
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException ex) {
        log.warn("[ExceptionHandler] Bad request: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    // 400 — sprint business rule violation
    @ExceptionHandler(SprintService.ValidationException.class)
    public ResponseEntity<?> sprintValidation(SprintService.ValidationException ex) {
        log.warn("[ExceptionHandler] Sprint validation error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    // 403 — ticket forbidden
    @ExceptionHandler(TicketService.ForbiddenException.class)
    public ResponseEntity<?> forbidden(TicketService.ForbiddenException ex) {
        log.warn("[ExceptionHandler] Forbidden: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    // 404 — sprint not found
    @ExceptionHandler(SprintService.DoesnotExistException.class)
    public ResponseEntity<?> sprintNotFound(SprintService.DoesnotExistException ex) {
        log.warn("[ExceptionHandler] Not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    // 409 — sprint conflict
    @ExceptionHandler(SprintService.ConflictException.class)
    public ResponseEntity<?> sprintConflict(SprintService.ConflictException ex) {
        log.warn("[ExceptionHandler] Conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    // 500 — catch-all (moved to bottom, most specific handlers above)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> server(RuntimeException ex) {
        log.error("[ExceptionHandler] Unexpected server error", ex); // full stack trace
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "An unexpected error occurred")); // don't leak ex.getMessage()
    }
}