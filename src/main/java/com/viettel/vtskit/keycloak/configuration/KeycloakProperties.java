package com.viettel.vtskit.keycloak.configuration;

import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

//@ConfigurationProperties(prefix = "vtskit.keycloak")
public class KeycloakProperties extends KeycloakSpringBootProperties {

    /**
     * Validate properties at here if necessary
     */
    private void validateProperties(){

    }

    @PostConstruct
    void init(){
        validateProperties();
    }

}
