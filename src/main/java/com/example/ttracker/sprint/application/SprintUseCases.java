package com.example.ttracker.sprint.application;

import com.example.ttracker.sprint.domain.Sprint;
import com.example.ttracker.ticket.domain.Ticket;
import com.example.ttracker.sprint.domain.CreateSprintCommand;
import com.example.ttracker.sprint.domain.UpdateSprintCommand;

import java.util.List;

public interface SprintUseCases {
    Sprint create(CreateSprintCommand command);
    Boolean existById(Long id);
    Sprint getById(Long id);
    List<Sprint> list();
    Sprint update(Long sprintId, UpdateSprintCommand command);
    void delete(Long sprintId);
    Long getBacklogSprintId();
    Boolean hasTickets(Long sprintId);
     List<Ticket> listTickets(Long sprintId );
}
