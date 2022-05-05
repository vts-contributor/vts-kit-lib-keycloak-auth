package com.viettel.vtskit.keycloak;

import com.viettel.vtskit.keycloak.configuration.ConstantConfiguration;
import com.viettel.vtskit.keycloak.configuration.KeycloakProperties;
import org.springframework.beans.factory.annotation.Autowired;

public class KeycloakService {

    private KeycloakProperties keycloakProperties;

    public String exampleFunction(String name){
        return String.format(ConstantConfiguration.GREETING_MESSAGE, name);
    }

    @Autowired
    public void setKeycloakProperties(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }
}
