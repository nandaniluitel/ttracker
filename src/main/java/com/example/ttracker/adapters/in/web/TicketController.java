package com.example.ttracker.adapters.in.web;

import com.example.ttracker.application.port.in.tickets.ChangeTicketStatusCommand;
import com.example.ttracker.application.port.in.tickets.CreateTicketCommand;
import com.example.ttracker.application.port.in.tickets.TicketFilter;
import com.example.ttracker.application.port.in.tickets.TicketUseCases;
import com.example.ttracker.application.port.in.tickets.UpdateTicketCommand;
import com.example.ttracker.domain.model.Priority;
import com.example.ttracker.domain.model.Ticket;
import com.example.ttracker.domain.model.TicketStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketUseCases tickets;

    public TicketController(TicketUseCases tickets) {
        this.tickets = tickets;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','SCRUM_MASTER','ADMIN')")
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest req) {
        Ticket t = tickets.create(req.toCommand());
        return ResponseEntity.status(201).body(TicketResponse.from(t));
    }

    @GetMapping
    public ResponseEntity<List<TicketResponse>> list(
        @RequestParam(required = false) Long sprintId,
        @RequestParam(required = false) Long epicId,
        @RequestParam(required = false) Long assigneeUserId,
        @RequestParam(required = false) TicketStatus status,
        @RequestParam(required = false) Priority priority
    ) {
        var filter = new TicketFilter(sprintId, epicId, assigneeUserId, status, priority);
        return ResponseEntity.ok(tickets.list(filter).stream().map(TicketResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(TicketResponse.from((tickets.getById(id))));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> changeStatus(@PathVariable Long id, @Valid @RequestBody
    ChangeTicketStatusCommand command) {
        Ticket updated = tickets.changeStatus(id, command.status());
        return ResponseEntity.ok(TicketResponse.from(updated));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','SCRUM_MASTER','ADMIN')")
    public ResponseEntity<TicketResponse> update(@PathVariable Long id,
        @RequestBody UpdateTicketRequest req) {
        Ticket updated = tickets.update(id, req.toCommand());
        return ResponseEntity.ok(TicketResponse.from(updated));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id){
        tickets.delete(id);
    }

    public record CreateTicketRequest(@NotBlank String title, @NotBlank String description, Priority priority,
                                      @Min(0) Integer storyPoints, Long assigneeUserId, Long epicId, Long sprintId) {
        public CreateTicketCommand toCommand() {
            return new CreateTicketCommand(title, description, priority, storyPoints, assigneeUserId, epicId, sprintId);
        }
    }


    public record TicketResponse(
        Long id,
        String title,
        String description,
        TicketStatus status,
        Priority priority,
        Integer storyPoints,
        Long assigneeUserId,
        Long epicId,
        Long sprintId,
        Long createdByUserId,
        Long editedByUserId
    ) {
        public static TicketResponse from(Ticket t) {
            return new TicketResponse(
                t.id(),
                t.title(),
                t.description(),
                t.status(),
                t.priority(),
                t.storyPoints(),
                t.assigneeUserId(),
                t.epicId(),
                t.sprintId(),
                t.createdByUserId(),
                t.editedByUserId()
            );
        }
    }

    public record ChangeStatusRecord(@NotBlank TicketStatus status) {
    }

    public record UpdateTicketRequest(String title,
                                      String description,
                                      Priority priority,
                                      @Min(0) Integer storyPoints,
                                      Long assigneeUserId,
                                      Long epicId,
                                      Long sprintId) {
        public UpdateTicketCommand toCommand() {
            return new UpdateTicketCommand(
                title,
                description,
                priority,
                storyPoints,
                assigneeUserId,
                epicId,
                sprintId
            );
        }
    }
}
