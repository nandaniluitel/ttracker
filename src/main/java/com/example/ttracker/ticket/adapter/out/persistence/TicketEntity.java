package com.example.ttracker.ticket.adapter.out.persistence;

import com.example.ttracker.security.domain.model.Priority;
import com.example.ttracker.ticket.domain.Ticket;
import com.example.ttracker.ticket.domain.TicketStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tickets",
    indexes = {
        @Index(name = "ix_tickets_epic_id", columnList = "epic_id"),
        @Index(name = "ix_tickets_sprint_id", columnList = "sprint_id"),
        @Index(name = "ix_tickets_assignee_user_id", columnList = "assignee_user_id"),
        @Index(name = "ix_tickets_status", columnList = "status")
    })
public class TicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Priority priority;

    @Column(name = "story_points")
    private Integer storyPoints;

    @Column(name = "assignee_user_id")
    private Long assigneeUserId;

    @Column(name = "epic_id")
    private Long epicId;

    @Column(name = "sprint_id", nullable = false)
    private Long sprintId;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "edited_by_user_id")
    private Long editedByUserId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;


    public TicketEntity() {
    }

    public TicketEntity(Long id, String title, String description, TicketStatus status, Priority priority,
        Integer storyPoints, Long assigneeUserId, Long epicId, Long sprintId, Long createdByUserId, Long editedByUserId,
        Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.storyPoints = storyPoints;
        this.assigneeUserId = assigneeUserId;
        this.epicId = epicId;
        this.sprintId = sprintId;
        this.createdByUserId = createdByUserId;
        this.editedByUserId = editedByUserId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TicketEntity from(Ticket ticket) {
        return new TicketEntity(ticket.id(), ticket.title(), ticket.description(), ticket.status(),ticket.priority(),
            ticket.storyPoints(),ticket.assigneeUserId(),ticket.epicId(),ticket.sprintId(),  ticket.createdByUserId(),ticket.editedByUserId(),ticket.createdAt(),ticket.updatedAt());
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

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Long getAssigneeUserId() {
        return assigneeUserId;
    }

    public void setAssigneeUserId(Long assigneeUserId) {
        this.assigneeUserId = assigneeUserId;
    }

    public Long getEpicId() {
        return epicId;
    }

    public void setEpicId(Long epicId) {
        this.epicId = epicId;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public Long getEditedByUserId() {
        return editedByUserId;
    }

    public void setEditedByUserId(Long editedByUserId) {
        this.editedByUserId = editedByUserId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Ticket toDomain() {
        return new Ticket(this.getId(), this.getTitle(), this.getDescription(),
                this.getStatus(), this.getPriority(),this.getStoryPoints(),this.assigneeUserId,this.getEpicId(),this.getSprintId(), this.getCreatedByUserId(),this.getEditedByUserId(),this.getCreatedAt(),this.getUpdatedAt());
    }
}
