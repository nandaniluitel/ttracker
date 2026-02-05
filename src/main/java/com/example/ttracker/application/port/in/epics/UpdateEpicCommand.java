package com.example.ttracker.application.port.in.epics;

import com.example.ttracker.domain.model.EpicStatus;
import com.example.ttracker.domain.model.Priority;

public record UpdateEpicCommand(
    String title,            // nullable
    String description,      // nullable
    EpicStatus status,       // nullable
    Priority priority,       // nullable
    Long assigneeUserId      // nullable
) {
}
