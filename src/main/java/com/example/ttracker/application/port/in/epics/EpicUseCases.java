package com.example.ttracker.application.port.in.epics;

import com.example.ttracker.application.port.in.tickets.UpdateTicketCommand;
import com.example.ttracker.domain.model.Epic;
import java.util.List;

public interface EpicUseCases
{
    Epic create(CreateEpicCommand command);
    Epic  getById(Long id);
    List<Epic> list();
    Epic update(Long epicId, UpdateEpicCommand cmd);

    void delete(Long epicId);

}
