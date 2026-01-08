package com.example.ttracker.adapters.in.web;

import com.example.ttracker.application.port.in.CreateTicketCommand;
import com.example.ttracker.application.port.in.TicketUseCases;
import com.example.ttracker.domain.model.Ticket;
import com.example.ttracker.domain.model.TicketStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketUseCases tickets;

    public TicketController(TicketUseCases tickets) {
        this.tickets = tickets;
    }
    public record CreateTicketRequest(@NotBlank String title, @NotBlank String description){}
    public record TicketResponse(Long id, String title,String description,String status, Long createdByUserId){}
    public record ChangeStatusRecord(@NotBlank String status){}
    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest req){
        Ticket t=tickets.create(new CreateTicketCommand(req.title(), req.description()));
        return ResponseEntity.status(201).body(toResponse(t));
    }
    @GetMapping
    public ResponseEntity<List<TicketResponse>> list(){
        return ResponseEntity.ok(tickets.list().stream().map(this::toResponse).toList());
    }
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable Long id){
        return ResponseEntity.ok(toResponse(tickets.getById(id)));
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> changeStatus(@PathVariable Long id, @Valid @RequestBody ChangeStatusRecord req){
        TicketStatus newStatus = TicketStatus.valueOf(req.status.trim().toUpperCase());
        Ticket updated=tickets.changeStatus(id,newStatus);
        return ResponseEntity.ok(toResponse(updated));
    }

    private TicketResponse toResponse(Ticket t)
    {
        return new TicketResponse(t.id(),t.title(),t.description(),t.status().name(),t.createdByUserId());
    }
}
