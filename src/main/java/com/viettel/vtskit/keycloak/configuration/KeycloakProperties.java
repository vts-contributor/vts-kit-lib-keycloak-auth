package com.viettel.vtskit.keycloak.configuration;

import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
public class KeycloakProperties extends KeycloakSpringBootProperties {

    private String adminRealm;
    private String adminClientId;
    private String adminClientSecret;
    private String adminUsername;
    private String adminPassword;
    private List<String> ignore;

    /**
     * Validate properties at here if necessary
     */
    private void validateProperties(){

    }

    @PostConstruct
    void init(){
        validateProperties();
    }

    public List<String> getIgnore() {
        return ignore;
    }

    public void setIgnore(List ignore) {
        this.ignore = ignore;
    }

    public String getAdminRealm() {
        return adminRealm;
    }

    public void setAdminRealm(String adminRealm) {
        this.adminRealm = adminRealm;
    }

    public String getAdminClientId() {
        return adminClientId;
    }

    public void setAdminClientId(String adminClientId) {
        this.adminClientId = adminClientId;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getAdminClientSecret() {
        return adminClientSecret;
    }

    public void setAdminClientSecret(String adminClientSecret) {
        this.adminClientSecret = adminClientSecret;
    }
}
