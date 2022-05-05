package com.viettel.vtskit.keycloak.configuration;

import com.viettel.vtskit.keycloak.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakAutoConfiguration {

    private KeycloakProperties keycloakProperties;

    @Bean
    public KeycloakService keycloakService(){
        return new KeycloakService();
    }

    @Autowired
    public void setKeycloakProperties(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }
}
