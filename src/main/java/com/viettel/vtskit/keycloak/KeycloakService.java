package com.viettel.vtskit.keycloak;

import com.fasterxml.jackson.databind.util.TypeKey;
import com.viettel.vtskit.keycloak.configuration.KeycloakProperties;
import com.viettel.vtskit.keycloak.utils.KeycloakUtils;
import org.json.JSONObject;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.authorization.client.resource.ProtectionResource;
import org.keycloak.authorization.client.util.Http;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.RefreshToken;
import org.keycloak.representations.idm.ClientMappingsRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import javax.ws.rs.core.Response;
import java.util.*;

public class KeycloakService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakService.class);

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
    //get user by email and username
    public UserRepresentation getUserByUsername(String username){
        validateEnableAdminOperation();
        return kcRealmResource.users().search(username).get(0);
    }

    private UserRepresentation getUserByEmail(String email){
        validateEnableAdminOperation();
        return kcRealmResource.users().get(email).toRepresentation();
    }

    /**
     * createUser
     */
    public Response createNewUser(UserRepresentation user){
        validateEnableAdminOperation();
        //KeycloakPrincipal principal
        UsersResource usersResource = kcRealmResource.users();

        return usersResource.create(user);
    }

    /**
     * Enable User
     */


    /**
     * updateUser
     */
    public void updateUser(UserRepresentation userRepresentation){;
        validateEnableAdminOperation();
        UsersResource usersResource = kcRealmResource.users();
        UserResource userResource = usersResource.get(userRepresentation.getId());
        userResource.update(userRepresentation);
    }

    /**
     * get response login ( access token, refresh token )
     * @param userName
     * @param password
     * @return AccessTokenResponseAccessTokenResponse
     */

    public AccessTokenResponse obtainAccessToken(String userName, String password) {
        Assert.notNull(userName, "Username must not be null");
        Assert.notNull(password, "Password must not be null");
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
     * get new token from refreshToken
     * @param refreshToken
     * @return
     */
    public AccessTokenResponse refreshToken(String refreshToken) {
        String url = keycloakProperties.getAuthServerUrl() + "/realms/" + keycloakProperties.getRealm() + "/protocol/openid-connect/token";
        String clientId = keycloakProperties.getResource();
        String secret = keycloakProperties.getClientKeyPassword();

        Http http = new Http(kcConfig(), (params, headers) -> {});

        return http.<AccessTokenResponse>post(url)
                .authentication()
                .client()
                .form()
                .param("grant_type", "refresh_token")
                .param("refresh_token", refreshToken)
                .param("client_id", clientId)
                .param("client_secret", secret)
                .response()
                .json(AccessTokenResponse.class)
                .execute();
    }

//    @Bean
    public org.keycloak.authorization.client.Configuration kcConfig() {
        return new org.keycloak.authorization.client.Configuration(
                keycloakProperties.getAuthServerUrl(),
                keycloakProperties.getRealm(),
                keycloakProperties.getResource(),
                keycloakProperties.getCredentials(),
                null
        );
    }

    /**
     * create Role client
     * @param roleName
     */
    private void createRoleClient(String roleName){
        validateEnableAdminOperation();
        // Create the role
        RoleRepresentation clientRoleRepresentation = new RoleRepresentation();
        clientRoleRepresentation.setName(roleName);
        //clientRoleRepresentation.setClientRole(true);
        kcAdminClient.realm(keycloakProperties.getAdminRealm()).clients().findByClientId(keycloakProperties.getAdminClientId()).forEach(clientRepresentation ->
                kcAdminClient.realm(keycloakProperties.getAdminRealm()).clients().get(clientRepresentation.getId()).roles().create(clientRoleRepresentation)
        );
    }

    /**
     * create realm role
     * @param roleName
     */
    public void createRealmRole(String roleName){
        validateEnableAdminOperation();

        // Create the role
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(roleName);
        roleRepresentation.setDescription("");
        roleRepresentation.setComposite(false);
        kcRealmResource.roles().create(roleRepresentation);
//        kcAdminClient.realm(keycloakProperties.getAdminRealm()).roles().create(roleRepresentation);


    }

    /**
     * set Role User
     * @param userId
     * @param roleName
     */
    public void setRole(String userId, String roleName){
        String userRole = roleName;
        validateEnableAdminOperation();
        UserResource userResource=kcRealmResource.users().get(userId);
        List<RoleRepresentation> roleRepresentationList = userResource.roles().realmLevel().listAvailable();

        for (RoleRepresentation roleRepresentation : roleRepresentationList)
        {
            if (roleRepresentation.getName().equals(userRole))
            {
                userResource.roles().realmLevel().add(Arrays.asList(roleRepresentation));
                break;
            }
        }
    }

    /**
     * get Role of User ( role Realm )
     * @param userId
     */
    public List<RoleRepresentation> getRoleUser(String userId){
        UserResource userResource=kcRealmResource.users().get(userId);
        List<RoleRepresentation> roleRepresentationList = userResource.roles().getAll().getRealmMappings();
        return roleRepresentationList;
//        for (RoleRepresentation r: roleRepresentationList
//             ) {
//            System.out.println(r.getName());
//        }
    }

    /**
     * get Role of User ( role client )
     * @param userId
     */
    public void getRoleClientUser(String userId){
        UserResource userResource=kcRealmResource.users().get(userId);
//        List<RoleRepresentation> roleRepresentationList = userResource.roles().getAll().getRealmMappings();
        Map<String, ClientMappingsRepresentation> roleRepresentationList = userResource.roles().getAll().getClientMappings();
        for (String name: roleRepresentationList.keySet()) {
            String key = name.toString();
            String value = roleRepresentationList.get(name).getMappings().toString();
            System.out.println(key + " " + value);
        }
    }
    /**
     * deleteUser
     */
    public void deleteUser(String userId){
        validateEnableAdminOperation();
        kcRealmResource.users().get(userId).remove();
    }

    /**
     * Lay thong tin user
     *
     * @param authentication
     * @return
     */
    public String getUserLogin(Authentication authentication) {
        try {
            KeycloakPrincipal principal = (KeycloakPrincipal) authentication.getPrincipal();
            String userName = principal.getKeycloakSecurityContext().getToken().getPreferredUsername().toUpperCase();
            return userName;
        } catch (Exception e) {
            LOGGER.error("Loi! getUserLogin: ", e);
            return null;
        }
    }

    /**
     * Lay thong tin user id
     *
     * @param authentication
     * @return
     */
    public  String getUserLoginId(Authentication authentication) {
        try {
            KeycloakPrincipal principal = (KeycloakPrincipal) authentication.getPrincipal();
            String userId = principal.getKeycloakSecurityContext().getToken().getId();
            return userId;
        } catch (Exception e) {
            LOGGER.error("Loi! getUserLoginId: ", e);
            return null;
        }
    }

    /**
     * Get string token
     *
     * @param authentication
     * @return
     */
    public  String getStringToken(Authentication authentication) {
        try {
            KeycloakPrincipal principal = (KeycloakPrincipal) authentication.getPrincipal();
            String strToken = principal.getKeycloakSecurityContext().getTokenString();
            return strToken;
        } catch (Exception e) {
            LOGGER.error("Loi! getUserLogin: ", e);
            return null;
        }
    }


    /**
     * Lay role
     * @param authentication
     * @return
     */
    public  Set<String> getRoleId(Authentication authentication) {
        try {
            KeycloakPrincipal principal = (KeycloakPrincipal) authentication.getPrincipal();
            Set<String> roleId = principal.getKeycloakSecurityContext().getToken().getResourceAccess().get(getClientId(authentication)).getRoles();
            return roleId;
        } catch (Exception e) {
            LOGGER.error("Loi! getUserLogin: ", e);
            return new HashSet<>();
        }
    }

    /**
     * Lay client id
     */
    public  String getClientId(Authentication authentication) {
        try {
            KeycloakPrincipal principal = (KeycloakPrincipal) authentication.getPrincipal();
            String clientId = principal.getKeycloakSecurityContext().getToken().getIssuedFor();
            return clientId;
        } catch (Exception e) {
            LOGGER.error("Loi! getUserLogin: ", e);
            return null;
        }
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
