package com.example.ttracker.adapters.out.persistence.entity;

import com.example.ttracker.domain.model.Epic;
import com.example.ttracker.domain.model.EpicStatus;
import com.example.ttracker.domain.model.Priority;
import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
@Entity
@Table(name="epics")
public class EpicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private EpicStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Priority priority;

    @Column(name = "assignee_user_id")
    private Long assigneeUserId; // nullable

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "edited_by_user_id")
    private Long editedByUserId; // nullable

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt; // nullable

    public EpicEntity() {
    }

    public EpicEntity(Long id, String title, String description, EpicStatus status, Priority priority,
        Long assigneeUserId,
        Long createdByUserId, Long editedByUserId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.assigneeUserId = assigneeUserId;
        this.createdByUserId = createdByUserId;
        this.editedByUserId = editedByUserId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public EpicStatus getStatus() {
        return status;
    }

    public void setStatus(EpicStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Long getAssigneeUserId() {
        return assigneeUserId;
    }

    public void setAssigneeUserId(Long assigneeUserId) {
        this.assigneeUserId = assigneeUserId;
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
    public static EpicEntity from(Epic epic){
        return new EpicEntity(epic.id(), epic.title(), epic.description(), epic.status(),epic.priority(),epic.assigneeUserId(),
            epic.createdByUserId(), epic.editedByUsedId(), epic.createdAt(),epic.updatedAt());
    }
    public static Epic toDomain(EpicEntity epicEntity)
    {
        return new Epic(epicEntity.getId(), epicEntity.getTitle(), epicEntity.getDescription(),epicEntity.getStatus(),epicEntity.getPriority(),
            epicEntity.getAssigneeUserId(), epicEntity.getCreatedByUserId(), epicEntity.getEditedByUserId(),epicEntity.getCreatedAt(),epicEntity.getUpdatedAt());
    }
}

