package com.example.ttracker.adapters.out.persistence.entity;

import com.example.ttracker.domain.model.Sprint;
import com.example.ttracker.domain.model.SprintStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "sprints")
public class SprintEntity {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, length = 128,unique = true)
        private String title;

        @Column(length = 255)
        private String goal; // nullable

        @Column(name = "start_date")
        private LocalDate startDate; // nullable

        @Column(name = "end_date")
        private LocalDate endDate; // nullable

        @Enumerated(EnumType.STRING)
        @Column(length = 32)
        private SprintStatus status; // nullable

        @Column(name = "created_by_user_id", nullable = false)
        private Long createdByUserId;

        @Column(name = "edited_by_user_id",nullable=true)
        private Long editedByUserId; // nullable

        @Column(name = "created_at", nullable = false)
        private Instant createdAt;

        @Column(name = "updated_at",nullable=true)
        private Instant updatedAt; // nullable

    public SprintEntity() {
    }

    public SprintEntity(Long id, String title, String goal, LocalDate startDate, LocalDate endDate, SprintStatus status,
        Long createdByUserId, Long editedByUserId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.goal = goal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
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

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public SprintStatus getStatus() {
        return status;
    }

    public void setStatus(SprintStatus status) {
        this.status = status;
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

    public Sprint toDomain() {
        return new Sprint(
            id, title, goal, startDate, endDate, status,
            createdByUserId, editedByUserId, createdAt, updatedAt
        );
    }

    public static SprintEntity fromDomain(Sprint s) {
        return new SprintEntity(
            s.id(), s.title(), s.goal(), s.startDate(), s.endDate(), s.status(),
            s.createdByUserId(), s.editedByUserId(), s.createdAt(), s.updatedAt()
        );
    }
}
