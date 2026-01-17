package com.example.ttracker.adapters.out.persistence.entity;

import com.example.ttracker.domain.model.Ticket;
import com.example.ttracker.domain.model.TicketStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tickets")
public class TicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String status;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    public TicketEntity() {
    }

    public TicketEntity(Long id, String title, String description, String status, Instant createdAt, Long createdByUserId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.createdByUserId = createdByUserId;
    }

    public static TicketEntity from(Ticket ticket) {
        return new TicketEntity(ticket.id(), ticket.title(), ticket.description(), ticket.status().name(), ticket.createdAt(), ticket.createdByUserId());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Ticket toDomain() {
        return new Ticket(this.getId(), this.getTitle(), this.getDescription(),
                TicketStatus.valueOf(this.getStatus()), this.getCreatedAt(), this.getCreatedByUserId());
    }
}
