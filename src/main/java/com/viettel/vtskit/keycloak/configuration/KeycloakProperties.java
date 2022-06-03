package com.viettel.vtskit.keycloak.configuration;

import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.List;

public class KeycloakProperties extends KeycloakSpringBootProperties {


    @Value("vtskit.keycloak.admin-realm:")
    private String adminRealm;
    @Value("vtskit.keycloak.admin-clientId:")
    private String adminClientId;
    @Value("vtskit.keycloak.admin-username:")
    private String adminUsername;
    @Value("vtskit.keycloak.admin-password:")
    private String adminPassword;

    @Value("vtskit.keycloak.ignore:")
    private List ignore;

    public List getIgnore() {
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
