package com.rafee.blocalert.blocalert.service.impl;

import com.rafee.blocalert.blocalert.entity.EmailLog;
import com.rafee.blocalert.blocalert.repository.EmailLogRepository;
import com.rafee.blocalert.blocalert.service.EmailLogService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailLogServiceImpl implements EmailLogService {

    private final EmailLogRepository emailLogRepository;

    @Async
    @Override
    public void saveLogs(List<EmailLog> emailLogList) {

        log.info("saveLogs called for {} email logs", emailLogList.size()); // TODO : Partition for large data set
        emailLogRepository.saveAll(emailLogList);
    }

    @Transactional
    @Override
    public void saveLog(EmailLog emailLog) {
        emailLogRepository.save(emailLog);
    }
}
