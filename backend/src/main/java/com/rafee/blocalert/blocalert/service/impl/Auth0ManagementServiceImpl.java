package com.rafee.blocalert.blocalert.service.impl;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.filter.UserFilter;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.roles.RolesPage;
import com.auth0.json.mgmt.tickets.EmailVerificationTicket;
import com.auth0.json.mgmt.tickets.PasswordChangeTicket;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;
import com.rafee.blocalert.blocalert.config.AppConfig;
import com.rafee.blocalert.blocalert.exception.ExternalApiException;
import com.rafee.blocalert.blocalert.exception.ResourceNotFoundException;
import com.rafee.blocalert.blocalert.service.Auth0ManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class Auth0ManagementServiceImpl implements Auth0ManagementService {

    private final Auth0Service auth0Service;
    private final AppConfig appConfig;

    @Value("${auth0.role-id.premium}")
    private String premiumRoleId;

    @Value("${auth0.role-id.free}")
    private String freeRoleId;

    @Value("${auth0.management.client-id}")
    private String clientId;

    @Override
    public void updateAuth0RoleToPremium(String auth0Id) throws Auth0Exception {

        log.debug("updateAuth0RoleToPremium called for {}", auth0Id);

        ManagementAPI managementAPI = auth0Service.getManagementAPI();

        assertUserExists(managementAPI, auth0Id);

        if (hasRole(managementAPI, auth0Id, premiumRoleId)) {
            throw new IllegalStateException("User already has premium role");
        }

        removeRole(managementAPI, auth0Id, freeRoleId);
        addRole(managementAPI, auth0Id, premiumRoleId);

    }

    @Override
    public void updateAuth0RoleToFree(String auth0Id) throws Auth0Exception {

        log.info("updateAuth0RoleToFree called for {}", auth0Id);

        ManagementAPI managementAPI = auth0Service.getManagementAPI();

        assertUserExists(managementAPI, auth0Id);

        if (hasRole(managementAPI, auth0Id, freeRoleId)) {
            throw new IllegalStateException("User already has free role");
        }

        removeRole(managementAPI, auth0Id, premiumRoleId);
        addRole(managementAPI, auth0Id, freeRoleId);

    }

    @Override
    public String getEmailVerificationLink(String auth0Id) throws Auth0Exception {

        log.info("sendEmailVerificationLink called for {}", auth0Id);

        ManagementAPI managementAPI = auth0Service.getManagementAPI();

        EmailVerificationTicket ticket = new EmailVerificationTicket(auth0Id);
        ticket.setResultUrl(appConfig.getFrontendUrl());
        ticket.setIncludeEmailInRedirect(false);
        ticket.setTTLSeconds(7 * 24 * 60 * 60);

        EmailVerificationTicket verificationTicket = managementAPI.tickets().requestEmailVerification(ticket).execute().getBody();

        return verificationTicket.getTicket();
    }

    @Override
    public String getResetPasswordLink(String auth0Id) throws Auth0Exception {

        log.info("getResetPasswordLink called for {}", auth0Id);

        ManagementAPI managementAPI = auth0Service.getManagementAPI();

        PasswordChangeTicket ticket = new PasswordChangeTicket(auth0Id);
        ticket.setResultUrl(appConfig.getFrontendUrl());
        ticket.setIncludeEmailInRedirect(false);
        ticket.setTTLSeconds(24 * 60 * 60);

        PasswordChangeTicket resetTicket = managementAPI.tickets().requestPasswordChange(ticket).execute().getBody();

        return resetTicket.getTicket();

    }

    private boolean hasRole(ManagementAPI managementAPI, String auth0Id, String targetRoleId) throws Auth0Exception {

        Request<RolesPage> request = managementAPI.users().listRoles(auth0Id, null);
        RolesPage rolesPage = request.execute().getBody();

        if (rolesPage == null) throw new ResourceNotFoundException("User Roles not found for the Auth0Id " + auth0Id);

        return rolesPage.getItems().stream()
                .anyMatch(role -> targetRoleId.equals(role.getId()));

    }

    private void assertUserExists(ManagementAPI managementAPI, String auth0Id) {
        try {
            User user = managementAPI.users()
                    .get(auth0Id, new UserFilter())
                    .execute()
                    .getBody();

            if (user == null) {
                throw new ResourceNotFoundException("User not found with Auth0 Id: " + auth0Id);
            }

        } catch (Exception e) {
            log.error("Failed to verify user existence for Auth0 Id: {}", auth0Id, e);
            throw new ExternalApiException("Failed to verify user existence from Auth0 " + auth0Id);
        }
    }

    private void removeRole(ManagementAPI managementAPI, String auth0Id, String targetRoleId) throws Auth0Exception {

        log.info("removeRole called for auth0Id : {}, targetRoleId : {}", auth0Id, targetRoleId);
        managementAPI.users().removeRoles(auth0Id, List.of(targetRoleId)).execute();
        log.info("Role removed");
    }

    private void addRole(ManagementAPI managementAPI, String auth0Id, String targetRoleId) throws Auth0Exception {

        log.info("addRole called for auth0Id : {}, targetRoleId : {}", auth0Id, targetRoleId);
        managementAPI.users().addRoles(auth0Id, List.of(targetRoleId)).execute();
        log.info("Role Added");
    }

}
