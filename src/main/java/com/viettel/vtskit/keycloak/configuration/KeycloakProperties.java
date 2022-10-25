package com.viettel.vtskit.keycloak.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@JsonPropertyOrder({"realm", "realm-public-key", "auth-server-url", "ssl-required", "resource", "public-client", "credentials", "use-resource-role-mappings", "enable-cors", "cors-max-age", "cors-allowed-methods", "cors-exposed-headers", "expose-token", "bearer-only", "autodetect-bearer-only", "connection-pool-size", "socket-timeout-millis", "connection-ttl-millis", "connection-timeout-millis", "allow-any-hostname", "disable-trust-manager", "truststore", "truststore-password", "client-keystore", "client-keystore-password", "client-key-password", "always-refresh-token", "register-node-at-startup", "register-node-period", "token-store", "adapter-state-cookie-path", "principal-attribute", "proxy-url", "turn-off-change-session-id-on-login", "token-minimum-time-to-live", "min-time-between-jwks-requests", "public-key-cache-ttl", "policy-enforcer", "ignore-oauth-query-parameter", "verify-token-audience"})
public class KeycloakProperties extends KeycloakSpringBootProperties {
    @JsonProperty("admin-realm")
    private String adminRealm;
    @JsonProperty("admin-client-id")
    private String adminClientId;
    @JsonProperty("client-key-password")
    private String adminClientSecret;
    @JsonProperty("admin-username")
    private String adminUsername;
    @JsonProperty("admin-password")
    private String adminPassword;
    @JsonProperty("ignore")
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
