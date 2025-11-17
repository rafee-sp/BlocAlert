package com.rafee.blocalert.blocalert.exception;

public class AlertLimitExceedException extends RuntimeException {

    public AlertLimitExceedException(String message) {
        super(message);
    }
}
