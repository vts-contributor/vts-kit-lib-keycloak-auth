package com.viettel.vtskit.keycloak;

import com.viettel.vtskit.keycloak.configuration.KeycloakClient;
import com.viettel.vtskit.keycloak.configuration.KeycloakProperties;
import org.json.JSONObject;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response;
import java.util.Base64;


public class KeycloakService {

    @Autowired
    private KeycloakClient keycloakClient;

    @Autowired
    private Keycloak keycloakAdminClient;

    @Autowired
    private RealmResource keycloakRealmResource;
    @Autowired
    private RestTemplate restTemplate;
    private KeycloakProperties keycloakProperties;

    @Autowired
    public void setKeycloakProperties(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }


    /**
     * getUserInfo
     *
     * @return
     */
    private JSONObject jsonParseToken(String token){
        String[] tokenWithoutBearer = token.split(" ");
        String[] chunks = tokenWithoutBearer[1].split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        JSONObject jsonObject=new JSONObject(payload);
        return jsonObject;
    }
    public UserRepresentation getUserInfo(String token) {
        JSONObject jsonObject=jsonParseToken(token);
        return keycloakRealmResource.users().get(jsonObject.get("sub").toString()).toRepresentation();
    }

    /**
     * getCurrentUser
     */
    public Authentication getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    private String getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SimpleKeycloakAccount details = (SimpleKeycloakAccount) authentication.getDetails();
        return details.getPrincipal().getName();
    }

    /**
     * authenticate
     */
    public AccessTokenResponse authenticate(String userName,String password) {

        Assert.notNull(userName, "Username is null");
        Assert.notNull(password, "Password is null");

        AuthzClient authzClient = keycloakClient.authzClient();
        AccessTokenResponse authResponse = authzClient.obtainAccessToken(userName, password);
        return authResponse;
    }

    /**
     * logout current user login
     */
    public Boolean logout(){
        try{
            String userId=getCurrentUserId();
            UserResource userResource=keycloakRealmResource.users().get(userId);
            userResource.logout();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * logout with specific user
     */
    public Boolean logout(String userId){
        try{
            UserResource userResource=keycloakRealmResource.users().get(userId);
            userResource.logout();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * logout session
     */
    public Boolean logoutToken(String token){
        try{
            JSONObject jsonObject=jsonParseToken(token);
            keycloakAdminClient.realm(keycloakProperties.getRealm()).deleteSession(jsonObject.get("sid").toString());
            return true;
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * refreshToken
     */
    public AccessTokenResponse refreshToken(String refreshToken) {
        Assert.notNull(refreshToken, "Refresh token is null");

        MultiValueMap refreshTokenRequest = keycloakClient.getClient();
        refreshTokenRequest.set("refresh_token", refreshToken);
        refreshTokenRequest.set("grant_type", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        headers.set(HttpHeaders.ACCEPT,MediaType.APPLICATION_JSON_VALUE);
        HttpEntity request = new HttpEntity<>(refreshTokenRequest,headers);
        ResponseEntity<AccessTokenResponse> authResponse =  restTemplate.postForEntity(keycloakClient.getTokenUrl(),request, AccessTokenResponse.class);

        return authResponse.getBody();
    }

    /**
     * changePassword
     */
    public void changePassword(String password){
        String userId= getCurrentUserId();

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        UsersResource usersResource = keycloakRealmResource.users();
        UserResource userResource = usersResource.get(userId);
        userResource.resetPassword(passwordCred);
    }

    /**
     * createUser
     */
    public Response createUser(UserRepresentation user){
        UsersResource usersResource = keycloakRealmResource.users();
        Response response = usersResource.create(user);
        return response;
    }
    /**
     * updateUser
     */
    public Boolean updateUser(UserRepresentation userRepresentation){;
        try{
            UsersResource usersResource = keycloakRealmResource.users();
            UserResource userResource=usersResource.get(userRepresentation.getId());
            userResource.update(userRepresentation);
            return true;
        }
        catch (Exception e){
            throw e;
        }
    }

    /**
     * deleteUser
     */
    public Boolean deleteUser(){
       try{
           String userId= getCurrentUserId();
           keycloakRealmResource.users().get(userId).remove();
           return true;
       }catch (Exception e){
           throw e;
       }
    }





}
