package com.viettel.vtskit.keycloak.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class KeycloakAuthentication implements Authentication {

    @JsonIgnore
    private KeycloakAuthenticationToken authenticationToken;

    public KeycloakAuthentication(KeycloakAuthenticationToken authenticationToken) {

        this.authenticationToken = authenticationToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authenticationToken.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        SimpleKeycloakAccount details = (SimpleKeycloakAccount) authenticationToken.getDetails();
        return details.getKeycloakSecurityContext().getToken();
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticationToken.isAuthenticated();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        authenticationToken.setAuthenticated(isAuthenticated);
    }

    @Override
    public String getName() {
        SimpleKeycloakAccount details = (SimpleKeycloakAccount) authenticationToken.getDetails();
        return details.getKeycloakSecurityContext().getToken().getPreferredUsername();
    }

    public String getId(){
        SimpleKeycloakAccount details = (SimpleKeycloakAccount) authenticationToken.getDetails();
        return details.getKeycloakSecurityContext().getToken().getId();
    }

}