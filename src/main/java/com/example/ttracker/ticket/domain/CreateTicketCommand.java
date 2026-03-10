package com.example.ttracker.ticket.domain;

import com.example.ttracker.security.domain.model.Priority;

public record CreateTicketCommand(
    String title,
    String description,
    Priority priority,//nullable->default MEDIUM in service
    Integer storyPoints,//nullable,must be>=0
    Long assigneeUserId,//nullable
    Long epicId,//nullable
    Long sprintId//nullable ->default Backlog in service

    ) {
    public CreateTicketCommand{
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Ticket title must not be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Ticket description must not be null or blank");
        }
    }
}
