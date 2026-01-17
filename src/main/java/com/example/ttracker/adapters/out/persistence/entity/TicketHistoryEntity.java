package com.example.ttracker.adapters.out.persistence.entity;

import com.example.ttracker.domain.model.TicketHistory;
import com.example.ttracker.domain.model.TicketHistoryAction;
import com.example.ttracker.domain.model.TicketStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "ticket_history")
public class TicketHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;

    @Column(nullable = false)
    private String action;

    @Column(name = "old_status")
    private String oldStatus;

    @Column(name = "new_status")
    private String newStatus;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @Column(name = "changed_by_user_id", nullable = false)
    private Long changedByUserId;

    public TicketHistoryEntity(Long id, Long ticketId, String action, String oldStatus, String newStatus,
                               Instant changedAt,
                               Long changedByUserId) {
        this.id = id;
        this.ticketId = ticketId;
        this.action = action;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedAt = changedAt;
        this.changedByUserId = changedByUserId;
    }

    public TicketHistoryEntity() {
    }

    public static TicketHistoryEntity from(TicketHistory ticketHistory) {
        return new TicketHistoryEntity(
                ticketHistory.id(),
                ticketHistory.ticketId(),
                ticketHistory.action().name(),
                ticketHistory.oldStatus() == null ? null : ticketHistory.oldStatus().name(),
                ticketHistory.newStatus() == null ? null : ticketHistory.newStatus().name(),
                ticketHistory.changedAt(),
                ticketHistory.changedByUserId());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    public Long getChangedByUserId() {
        return changedByUserId;
    }

    public void setChangedByUserId(Long changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    public TicketHistory toDomain() {
        return new TicketHistory(
                this.getId(),
                this.getTicketId(),
                TicketHistoryAction.valueOf(this.getAction()),
                this.getOldStatus() == null ? null : TicketStatus.valueOf(this.getOldStatus()),
                this.getNewStatus() == null ? null : TicketStatus.valueOf(this.getNewStatus()),
                this.getChangedAt(),
                this.getChangedByUserId());
    }
}
