package com.viettel.vtskit.keycloak.configuration;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class KeycloakClient {
    @Autowired
    KeycloakProperties keycloakProperties;

    @Bean
    public Keycloak keycloakAdminClient(){
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getAuthServerUrl())
                .realm(keycloakProperties.getAdminRealm())
                .clientId(keycloakProperties.getAdminClientId())
                .username(keycloakProperties.getAdminUsername())
                .password(keycloakProperties.getAdminPassword())
                .build();
    }
    public AuthzClient authzClient(){
        Map clientCredentials = new HashMap<String, Object>();
        clientCredentials.put("secret", keycloakProperties.getClientKeyPassword()==null?"":keycloakProperties.getClientKeyPassword());
        clientCredentials.put("grant_type", "password");
        Configuration configuration =
                new Configuration(keycloakProperties.getAuthServerUrl(), keycloakProperties.getRealm(),
                        keycloakProperties.getResource(), clientCredentials, null);
        return AuthzClient.create(configuration);
    }

    @Bean
    public RealmResource realmResource(Keycloak keycloak) {
        return keycloak.realm(keycloakProperties.getRealm());
    }

    public String getTokenUrl(){
        return keycloakProperties.getAuthServerUrl()+"/realms/"+keycloakProperties.getRealm()+"/protocol/openid-connect/token";
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    public MultiValueMap<String, String> getClient(){
        MultiValueMap map =  new LinkedMultiValueMap<String, String>();
        map.set("client_id", keycloakProperties.getResource());
        map.set("client_secret", keycloakProperties.getClientKeyPassword()==null?"":keycloakProperties.getClientKeyPassword());
        return map;
    }
}