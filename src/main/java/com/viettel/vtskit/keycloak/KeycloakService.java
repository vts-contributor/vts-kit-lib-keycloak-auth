package com.viettel.vtskit.keycloak;

import com.viettel.vtskit.keycloak.configuration.KeycloakProperties;
import com.viettel.vtskit.keycloak.utils.KeycloakUtils;
import org.json.JSONObject;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class KeycloakService {

    /**
     * Admin Operation
     */
    private Keycloak kcAdminClient;
    private RealmResource kcRealmResource;

    private KeycloakProperties keycloakProperties;

    private void validateEnableAdminOperation(){
        if(kcAdminClient == null){
            throw new IllegalArgumentException("Please configuration administrator account in application-*.yml");
        }
    }

    public Authentication getCurrentAuthentication(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    /**
     * logout with specific user
     */
    public void logoutByUserId(String userId){
        validateEnableAdminOperation();
        UserResource userResource=kcRealmResource.users().get(userId);
        userResource.logout();
    }

    /**
     * logout session
     */
    public void logoutToken(String token){
        validateEnableAdminOperation();
        JSONObject jsonObject = KeycloakUtils.parseJwtToken(token);
        String sessionId = jsonObject.getString("sid");
        kcAdminClient.realm(keycloakProperties.getRealm()).deleteSession(sessionId);
    }

    public UserRepresentation getUserByToken(String token) {
        validateEnableAdminOperation();
        JSONObject payload = KeycloakUtils.parseJwtToken(token);
        String subId = payload.getString("sub");
        return kcRealmResource.users().get(subId).toRepresentation();
    }

    /**
     * createUser
     */
    public Response createNewUser(UserRepresentation user){
        validateEnableAdminOperation();
        UsersResource usersResource = kcRealmResource.users();
        return usersResource.create(user);
    }

    /**
     * updateUser
     */
    public void updateUser(UserRepresentation userRepresentation){;
        validateEnableAdminOperation();
        UsersResource usersResource = kcRealmResource.users();
        UserResource userResource = usersResource.get(userRepresentation.getId());
        userResource.update(userRepresentation);
    }

    public AccessTokenResponse obtainAccessToken(String userName, String password) {
        Assert.notNull(userName, "Username must is not null");
        Assert.notNull(password, "Password must is not null");
        validateEnableAdminOperation();
        Map clientCredentials = new HashMap<String, Object>();
        clientCredentials.put("secret", keycloakProperties.getClientKeyPassword() == null ? "" : keycloakProperties.getClientKeyPassword());
        clientCredentials.put("grant_type", "password");
        Configuration configuration = new Configuration(keycloakProperties.getAuthServerUrl(),
                keycloakProperties.getRealm(),
                keycloakProperties.getResource(), clientCredentials, null);
        AuthzClient authzClient = AuthzClient.create(configuration);
        return authzClient.obtainAccessToken(userName, password);
    }

    /**
     * deleteUser
     */
    public void deleteUser(String userId){
        validateEnableAdminOperation();
        kcRealmResource.users().get(userId).remove();
    }

    @Autowired(required = false)
    public void setKcAdminClient(Keycloak kcAdminClient) {
        this.kcAdminClient = kcAdminClient;
    }

    @Autowired(required = false)
    public void setKcRealmResource(RealmResource kcRealmResource) {
        this.kcRealmResource = kcRealmResource;
    }

    @Autowired
    public void setKeycloakProperties(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

}
