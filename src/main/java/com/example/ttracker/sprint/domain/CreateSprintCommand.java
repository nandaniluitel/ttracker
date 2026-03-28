package com.example.ttracker.sprint.domain;
import java.time.LocalDate;

public record CreateSprintCommand(
    String title,
    String goal,
    LocalDate startDate,
    LocalDate endDate,
    SprintStatus status//nullable allowed
) {
    public CreateSprintCommand{
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Sprint title must not be null or blank");
        }
        if (goal == null || goal.isBlank()) {
            throw new IllegalArgumentException("Sprint goal must not be null or blank");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Epic startDate must not be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("Epic endDate can't be null");
        }
    }
}
