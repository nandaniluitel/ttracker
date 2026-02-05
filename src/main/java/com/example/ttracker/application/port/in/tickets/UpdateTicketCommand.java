package com.example.ttracker.application.port.in.tickets;

import com.example.ttracker.domain.model.EpicStatus;
import com.example.ttracker.domain.model.Priority;

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
