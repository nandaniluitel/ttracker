package com.example.ttracker.ticket.adapter.out.persistence;

import com.example.ttracker.security.domain.model.TicketHistory;
import com.example.ttracker.security.domain.model.TicketHistoryAction;
import com.example.ttracker.security.domain.model.TicketStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketHistoryAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status")
    private TicketStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private TicketStatus newStatus;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @Column(name = "changed_by_user_id", nullable = false)
    private Long changedByUserId;

    public TicketHistoryEntity(Long id, Long ticketId, TicketHistoryAction action, TicketStatus oldStatus,
        TicketStatus newStatus, Instant changedAt, Long changedByUserId) {
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
                ticketHistory.action(),
                ticketHistory.oldStatus() == null ? null : ticketHistory.oldStatus(),
                ticketHistory.newStatus() == null ? null : ticketHistory.newStatus(),
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

    public TicketHistoryAction getAction() {
        return action;
    }

    public void setAction(TicketHistoryAction action) {
        this.action = action;
    }

    public TicketStatus getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(TicketStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public TicketStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(TicketStatus newStatus) {
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
                this.getAction(),
                this.getOldStatus() == null ? null : this.getOldStatus(),
                this.getNewStatus() == null ? null : this.getNewStatus(),
                this.getChangedAt(),
                this.getChangedByUserId());
    }
}
