package com.rafee.blocalert.blocalert.service;

import com.rafee.blocalert.blocalert.entity.EmailLog;

import java.util.List;

public interface EmailLogService {

    void saveLogs(List<EmailLog> emailLogList);

    void saveLog(EmailLog emailLog);
}
