package com.rafee.blocalert.blocalert.service;

public interface AuthenticationService {

    Long getCurrentUserId();

    Long validateAndGetUserId(String token);
}
