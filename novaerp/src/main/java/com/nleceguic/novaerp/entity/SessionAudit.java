package com.nleceguic.novaerp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "session_audits")
public class SessionAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String ipAddress;

    private LocalDateTime loginTime;

    private LocalDateTime logoutTime;
}
