package com.example.ttracker.sprint.domain;
import com.example.ttracker.security.domain.model.SprintStatus;
import java.time.LocalDate;

public record CreateSprintCommand(
    String title,
    String goal,
    LocalDate startDate,
    LocalDate endDate,
    SprintStatus status//nullable allowed
) {
}
