package com.viettel.vtskit.keycloak.configuration;

import com.viettel.vtskit.keycloak.KeycloakService;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class KeycloakAutoConfiguration {

    @Bean
    public KeycloakService keycloakService(){
        return new KeycloakService();
    }
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "vtskit.keycloak", ignoreUnknownFields = true)
    public KeycloakSpringBootProperties keycloakSpringBootProperties(){
        KeycloakProperties keycloakProperties=new KeycloakProperties();
        keycloakProperties.setAdminClientId("master");
        keycloakProperties.setAdminClientId("admin-cli");
        return keycloakProperties;
    }
    /**
     * KeycloakConfigResolver defines that
     * we want to use the Spring Boot properties file support
     * instead of the default keycloak.json.
     */
    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        KeycloakSpringBootConfigResolver keycloakSpringBootConfigResolver= new KeycloakSpringBootConfigResolver();
        return keycloakSpringBootConfigResolver;
    }
}
