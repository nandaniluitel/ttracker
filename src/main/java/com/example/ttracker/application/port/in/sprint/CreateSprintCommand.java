package com.example.ttracker.application.port.in.sprint;
import com.example.ttracker.domain.model.SprintStatus;
import java.time.LocalDate;

public record CreateSprintCommand(
    String title,
    String goal,
    LocalDate startDate,
    LocalDate endDate,
    SprintStatus status//nullable allowed
) {
}
