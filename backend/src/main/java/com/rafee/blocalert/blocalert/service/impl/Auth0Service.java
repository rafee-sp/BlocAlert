package com.rafee.blocalert.blocalert.service.impl;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.TokenHolder;
import com.auth0.net.TokenRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Auth0Service {


    @Value("${auth0.management.domain}")
    private String domain;

    @Value("${auth0.management.client-id}")
    private String clientId;

    @Value("${auth0.management.client-secret}")
    private String clientSecret;

    @Value("${auth0.audience}")
    private String audience;


    public ManagementAPI getManagementAPI() { // Change to refreshing token

        try {

            AuthAPI authAPI = AuthAPI.newBuilder(domain, clientId, clientSecret).build();

            TokenRequest tokenRequest = authAPI.requestToken(audience);
            TokenHolder token = tokenRequest.execute().getBody();

            return ManagementAPI.newBuilder(domain, token.getAccessToken()).build();

        } catch (Auth0Exception e) {
            throw new RuntimeException("Failed to retrieve Auth0 Management API", e);
        }

    }

}
