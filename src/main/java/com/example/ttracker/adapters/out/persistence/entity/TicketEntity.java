package com.example.ttracker.adapters.out.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="tickets")
public class TicketEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String status;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }                      
}
