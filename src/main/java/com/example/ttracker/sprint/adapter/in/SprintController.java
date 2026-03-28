package com.example.ttracker.sprint.adapter.in;

import com.example.ttracker.sprint.application.SprintUseCases;
import com.example.ttracker.sprint.domain.CreateSprintCommand;
import com.example.ttracker.sprint.domain.UpdateSprintCommand;
import com.example.ttracker.sprint.domain.Sprint;
import com.example.ttracker.sprint.domain.SprintStatus;
import com.example.ttracker.ticket.adapter.in.TicketController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sprints")
public class SprintController {
    private final SprintUseCases sprints;

    public SprintController(SprintUseCases sprints) {
        this.sprints = sprints;
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public SprintResponse create(@Valid @RequestBody CreateSprintRequest req){
        Sprint created=sprints.create(new CreateSprintCommand(
            req.title(),
            req.goal(),
            req.startDate(),
            req.endDate(),
            req.status()
            ));
        return SprintResponse.from(created);
    }
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<SprintResponse> list(){
        return sprints.list().stream().map(SprintResponse::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public SprintResponse getById(@PathVariable Long id){
        return SprintResponse.from(sprints.getById(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public SprintResponse update(@PathVariable Long id, @Valid @RequestBody UpdateSprintRequest req) {
        Sprint updated = sprints.update(id, new UpdateSprintCommand(
            req.title(),
            req.goal(),
            req.startDate(),
            req.endDate(),
            req.status()
        ));
        return SprintResponse.from(updated);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('SCRUM_MASTER') or hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        sprints.delete(id);
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<List<TicketController.TicketResponse>> listTicketsInSprint(@PathVariable Long id){
        sprints.existById(id);
        var tickets=sprints.listTickets(id);
        return ResponseEntity.ok(tickets.stream().map(TicketController.TicketResponse::from).toList());
    }















    public record CreateSprintRequest(
        @NotBlank @Size(max = 128) String title,
        @Size(max = 255) String goal,
        LocalDate startDate,
        LocalDate endDate,
        // If you use SprintStatus enum end-to-end, change type to SprintStatus
        // and update command constructor accordingly.
        SprintStatus status
    ) {}
    public record UpdateSprintRequest(
        @Size(max = 128) String title,
        @Size(max = 255) String goal,
        LocalDate startDate,
        LocalDate endDate,
        SprintStatus status
    ) {}

    public record SprintResponse(
        Long id,
        String title,
        String goal,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        Long createdByUserId,
        Long editedByUserId,
        String createdAt,
        String updatedAt
    ){
        public static SprintResponse from(Sprint s){
            return new SprintResponse(
                s.id(),
                s.title(),
                s.goal(),
                s.startDate(),
                s.endDate(),
                s.status() == null ? null : s.status().name(),
                s.createdByUserId(),
                s.editedByUserId(),
                s.createdAt() == null ? null : s.createdAt().toString(),
                s.updatedAt() == null ? null : s.updatedAt().toString()
            );
        }
    }
}
