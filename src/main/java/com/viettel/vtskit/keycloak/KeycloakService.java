package com.viettel.vtskit.keycloak;

import com.viettel.vtskit.keycloak.configuration.ConstantConfiguration;
import com.viettel.vtskit.keycloak.configuration.KeycloakAuthentication;
import com.viettel.vtskit.keycloak.configuration.KeycloakProperties;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;

public class KeycloakService {

    private KeycloakProperties keycloakProperties;

    public String exampleFunction(String name){
        return String.format(ConstantConfiguration.GREETING_MESSAGE, name);
    }

    @Autowired
    public void setKeycloakProperties(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    public Authentication getUserInfo(Authentication authentication) {
        if(authentication instanceof KeycloakAuthenticationToken) {
            return new KeycloakAuthentication((KeycloakAuthenticationToken) authentication);
        }
        return authentication;
    }
}
