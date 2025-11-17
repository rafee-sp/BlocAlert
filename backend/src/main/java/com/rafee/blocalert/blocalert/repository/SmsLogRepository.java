package com.rafee.blocalert.blocalert.repository;

import com.rafee.blocalert.blocalert.entity.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsLogRepository extends JpaRepository<SmsLog, Long> {
}
