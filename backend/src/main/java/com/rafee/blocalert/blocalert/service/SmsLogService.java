package com.rafee.blocalert.blocalert.service;

import com.rafee.blocalert.blocalert.entity.SmsLog;

import java.util.List;

public interface SmsLogService {

    void saveLogs(List<SmsLog> smsLogList);
}
