package com.example.ttracker.application.port.in.sprint;

import com.example.ttracker.domain.model.Sprint;
import java.util.List;

public interface SprintUseCases {
    Sprint create(CreateSprintCommand command);
    Sprint getById(Long id);
    List<Sprint> list();
    Sprint update(Long sprintId, UpdateSprintCommand command);
    void delete(Long sprintId);

}
