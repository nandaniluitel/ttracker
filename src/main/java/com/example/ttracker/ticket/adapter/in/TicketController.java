package com.example.ttracker.ticket.adapter.in;

import com.example.ttracker.ticket.application.TicketService;
import com.example.ttracker.ticket.domain.ChangeTicketStatusCommand;
import com.example.ttracker.ticket.domain.CreateTicketCommand;
import com.example.ttracker.ticket.domain.TicketFilter;
import com.example.ttracker.ticket.domain.UpdateTicketCommand;
import com.example.ttracker.security.domain.model.Priority;
import com.example.ttracker.security.domain.model.Ticket;
import com.example.ttracker.security.domain.model.TicketStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService tickets) {
        this.ticketService = tickets;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','SCRUM_MASTER','ADMIN')")
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest req) {
        Ticket t = ticketService.create(req.toCommand());
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
        return ResponseEntity.ok(ticketService.list(filter).stream().map(TicketResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(TicketResponse.from((ticketService.getById(id))));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> changeStatus(@PathVariable Long id, @Valid @RequestBody
    ChangeTicketStatusCommand command) {
        Ticket updated = ticketService.changeStatus(id, command.status());
        return ResponseEntity.ok(TicketResponse.from(updated));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','SCRUM_MASTER','ADMIN')")
    public ResponseEntity<TicketResponse> update(@PathVariable Long id,
        @RequestBody UpdateTicketRequest req) {
        Ticket updated = ticketService.update(id, req.toCommand());
        return ResponseEntity.ok(TicketResponse.from(updated));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id){
        ticketService.delete(id);
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
