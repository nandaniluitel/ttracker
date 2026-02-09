package com.example.ttracker.ticket.domain;

import com.example.ttracker.security.domain.model.Priority;

public record UpdateTicketCommand(

    String title,             // nullable
    String description,       // nullable
    Priority priority,// nullable
    Integer storyPoints,
    Long assigneeUserId,      // nullable (can set/unset)

    Long epicId,
    Long sprintId
) {
}
