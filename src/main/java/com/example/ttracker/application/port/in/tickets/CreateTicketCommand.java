package com.example.ttracker.application.port.in.tickets;

import com.example.ttracker.domain.model.Priority;

public record CreateTicketCommand(
    String title,
    String description,
    Priority priority,//nullable->default MEDIUM in service
    Integer storyPoints,//nullable,must be>=0
    Long assigneeUserId,//nullable
    Long epicId,//nullable
    Long sprintId//nullable ->default Backlog in service

    ) {
}
