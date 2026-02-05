package com.example.ttracker.epic.domain;

import com.example.ttracker.security.domain.model.Priority;

public record CreateEpicCommand(
    String title,
    String description,
    Priority priority,
    Long assigneeUserId //nullable

) {

     public CreateEpicCommand {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Epic title must not be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Epic description must not be null or blank");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Epic priority must not be null");
        }
        if (assigneeUserId == null) {
            throw new IllegalArgumentException("Assignee user ID can't be null");
        }
     }

}
