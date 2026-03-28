package com.example.ttracker.sprint.adapter.out.persistence;
import com.example.ttracker.sprint.domain.Sprint;
import java.util.List;
import java.util.Optional;

public interface SprintRepositoryPort {
    Sprint save(Sprint sprint);
    Optional<Sprint>  findById(Long id);
    List<Sprint> findAll();
    Optional<Sprint> findByTitle(String title);
    boolean existsById(Long id);
    void deleteById(Long id);


}
