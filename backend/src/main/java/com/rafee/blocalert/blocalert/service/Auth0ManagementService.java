package com.rafee.blocalert.blocalert.service;

import com.auth0.exception.Auth0Exception;

public interface Auth0ManagementService {

    void updateAuth0RoleToPremium(String auth0Id) throws Auth0Exception;

    void updateAuth0RoleToFree(String auth0Id) throws Auth0Exception;

    String getEmailVerificationLink(String auth0Id) throws Auth0Exception;

    String getResetPasswordLink(String auth0Id) throws Auth0Exception;

}
