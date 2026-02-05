package com.example.ttracker.application.port.out;
import com.example.ttracker.domain.model.Sprint;
import java.util.List;
import java.util.Optional;

public interface SprintRepositoryPort {
    Sprint save(Sprint sprint);
    Optional<Sprint>  findById(Long id);
    List<Sprint> findAll();
    Optional<Sprint> findByTitle(String title);
    Long findBacklogSprintId(); // must exist
    boolean existsById(Long id);
    void deleteById(Long id);
    boolean hasTickets(Long sprintId);


}
