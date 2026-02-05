package com.example.ttracker.application.port.in.epics;

import com.example.ttracker.domain.model.Priority;
import com.example.ttracker.domain.model.TicketStatus;

public record CreateEpicCommand(
    String title,
    String description,
    Priority priority,
    Long assigneeUserId //nullable

) {


}
