package com.example.ttracker.epic.application;

import com.example.ttracker.security.domain.model.Epic;
import com.example.ttracker.epic.domain.CreateEpicCommand;
import com.example.ttracker.epic.domain.UpdateEpicCommand;

import java.util.List;

public interface EpicUseCases
{
    Epic create(CreateEpicCommand command);
    Epic  getById(Long id);
    List<Epic> list();
    Epic update(Long epicId, UpdateEpicCommand cmd);

    void delete(Long epicId);

}
