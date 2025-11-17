package com.rafee.blocalert.blocalert.repository;

import com.rafee.blocalert.blocalert.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
}
