package com.nleceguic.novaerp.repository;

import com.nleceguic.novaerp.entity.SessionAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionAuditRepository extends JpaRepository<SessionAudit, Long> {
}