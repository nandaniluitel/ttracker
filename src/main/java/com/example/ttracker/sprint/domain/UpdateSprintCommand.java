package com.example.ttracker.sprint.domain;

import com.example.ttracker.security.domain.model.SprintStatus;
import java.time.LocalDate;

public record UpdateSprintCommand(
    String title,        // nullable = no change
    String goal,         // nullable = no change
    LocalDate startDate, // nullable = no change
    LocalDate endDate,   // nullable = no change
    SprintStatus status  //nullable=no change
) {
}
