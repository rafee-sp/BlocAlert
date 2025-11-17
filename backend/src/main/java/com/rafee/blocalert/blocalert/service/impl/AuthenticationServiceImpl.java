package com.rafee.blocalert.blocalert.service.impl;

import com.rafee.blocalert.blocalert.exception.UnauthorizedException;
import com.rafee.blocalert.blocalert.service.AuthenticationService;
import com.rafee.blocalert.blocalert.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final JwtDecoder jwtDecoder;

    @Override
    public Long getCurrentUserId() {
        String auth0Id = getCurrentAuth0Id();
        return userService.getUserIdByAuth0Id(auth0Id);
    }

    @Override
    public Long validateAndGetUserId(String token) {

        try {

            if (!StringUtils.hasText(token)) return null;

            Jwt jwt = jwtDecoder.decode(token);

            String auth0Id = jwt.getSubject();

            if (!StringUtils.hasText(auth0Id)) return null;

            return userService.getUserIdByAuth0Id(auth0Id);

        } catch (JwtException e) {
            return null;
        } catch (Exception e) {
            log.error("Exception occurred ", e);
            return null;
        }
    }

    private String getCurrentAuth0Id() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("User is not authenticated");
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String subject = jwt.getSubject();
            if(!StringUtils.hasText(subject))
                throw new UnauthorizedException("JWT subject is missing");
            return subject;
        }
        throw new UnauthorizedException("Invalid authentication type");
    }
}
