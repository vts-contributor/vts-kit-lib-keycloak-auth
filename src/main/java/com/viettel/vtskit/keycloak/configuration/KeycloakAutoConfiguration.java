package com.viettel.vtskit.keycloak.configuration;

import com.viettel.vtskit.keycloak.KeycloakService;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import({KeycloakSecurityConfig.class})
public class KeycloakAutoConfiguration {
    private static final String DEFAULT_ADMIN_REAM = "master";
    private static final String DEFAULT_ADMIN_CLIENT = "admin-cli";

    @Bean
    public KeycloakService keycloakService(){
        return new KeycloakService();
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "vtskit.keycloak")
    public KeycloakProperties keycloakProperties(){
        KeycloakProperties keycloakProperties=new KeycloakProperties();
        keycloakProperties.setBearerOnly(true);
        keycloakProperties.setAdminRealm(DEFAULT_ADMIN_REAM);
        keycloakProperties.setAdminClientId(DEFAULT_ADMIN_CLIENT);
        return keycloakProperties;
    }

    @Bean
    @ConditionalOnProperty(prefix = "vtskit.keycloak", name = "admin-client-id")
    public Keycloak kcAdminClient(){
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties().getAuthServerUrl())
                .realm(keycloakProperties().getAdminRealm())
                .clientId(keycloakProperties().getAdminClientId())
                .clientSecret(keycloakProperties().getAdminClientSecret())
                .username(keycloakProperties().getAdminUsername())
                .password(keycloakProperties().getAdminPassword())
                .build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "vtskit.keycloak", name = "admin-client-id")
    public RealmResource realmResource(Keycloak keycloak) {
        return keycloak.realm(keycloakProperties().getRealm());
    }

    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }
}
