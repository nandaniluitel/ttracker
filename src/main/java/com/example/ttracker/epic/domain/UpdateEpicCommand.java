package com.example.ttracker.epic.domain;

import com.example.ttracker.security.domain.model.EpicStatus;
import com.example.ttracker.security.domain.model.Priority;

public record UpdateEpicCommand(
    String title,            // nullable
    String description,      // nullable
    EpicStatus status,       // nullable
    Priority priority,       // nullable
    Long assigneeUserId      // nullable
) {git
}
