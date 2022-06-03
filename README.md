Keycloak Auth Library for Spring Boot
-------
This library provides utilities that make it easy to integrate Keycloak into spring boot project

Feature List:
* [Secure REST API](#securing-rest-api-using-keycloak)
  * [Authenticate](#authenticate)
  * [Authorize](#authorize)
  * [Logout Current User](#Logout-current-user)
  * [Get Current User](#Get-current-user)
* [Admin Operation](#admin-operation )
  * [Login](#Login)
  * [Logout Specific User](#Logout-specific-user)
  * [Logout Specific Session](#Logout-specific-session)
  * [Get User Information](#Get-user-information)
  * [Refresh Token](#Refresh-token)
  * [Create User](#Create-User)
  * [Update User](#Update-User)
  * [Delete User](#Delete-User)

Quick start
-------
* Just add the dependency to an existing Spring Boot project
```xml
<dependency>
    <groupId>com.atviettelsolutions</groupId>
    <artifactId>vts-kit-ms-keycloak-auth</artifactId>
    <version>1.0.0</version>
</dependency>
```

* Then, add the following properties to your `application-*.yml` file.
```yaml
vtskit:
  keycloak:
    realm: keycloak-demo # realm name
    auth-server-url: http://localhost:8080 #url connect to server keycloak
    resource: client-keycloak  #client id
    bearer-only: true # only authenticate bearer token, not attempt login 
    client-key-password: fI4B6eDBtwEi0NJsJMmYNSiIgj3jVp28 # password of client
```

Usage
-------
##### Securing REST API using Keycloak
###### Authenticate
  By default, all API will be authenticated. To ignore authenticate in you api, you must add to `application-*.yml`:
  ```yaml
        vtskit:
          keycloak:
            ignore: ['/auth/**', '/create'] #list of ignored url
  ```
###### Authorize
To authorize your API: 
* Option 1: Define in your `application.yml` security constraint:
  ```yaml
      vtskit:
        keycloak:
          securityConstraints:
            - authRoles: [user] # list role will be accepted
              securityCollections:
                - name: API
                  patterns: ['/test/*'] #list pattern url user have role in authRoles will be accessed
  ```
* Option 2: You can use annotation: 
  ```java
      @RestController
      @RequestMapping("/")
      public class TestController {
          @GetMapping(path = "test")
          @RolesAllowed({"user"})
          public ResponseEntity test(){
                  return ResponseEntity.ok("test");
          }
      }
  ```
###### Logout Current User
  ```java
    keycloakService.logout()
  ```
###### Get Current User
  ```java
    keycloakService.getCurrentUser()
  ```
##### Admin Operation 
* Step 1: To use, you must add some information about account admin keycloak:
    ```yaml
    vtskit:
      keycloak:
        admin-realm: master # optional (default: master)
        admin-clientId: admin-cli #optional (default: admin-cli)
        admin-username: admin
        admin-password: admin
    ```
* Step 2: You need inject to you class KeycloakService:
  ```java
    @RestController
    @RequestMapping("/")
    public class TestController {
        @Autowired
        KeycloakService keycloakService;
    }
  ```
* Step 3: Use our built-in function:
  ###### Login
  ```java
  Authorization test = keycloakService.authenticate(String username, String password)
  ```
  ###### Logout Specific User
    ```java
    Boolean test =  keycloakService.logout(String UserId)
    ```
  ###### Logout Specific Session
    ```java
    Boolean test =  keycloakService.logoutToken(String token)
    ```
  ###### Get User Information
    ```java
    UserRepresentation test =  keycloakService.getUserInfo(String token)
    ```
  ###### Refresh Token
    ```java
    AccessTokenResponse test = keycloakService.refreshToken(String refreshToken)
    ```
  ###### Create User
    ```java
    Response test = keycloakService.createUser(UserRepresentation userRepresentation)
    ```
  ###### Update User
    ```java
    Boolean  test = keycloakService.updateUser(UserRepresentation userRepresentation)
    ```
  ###### Delete User
    ```java
    Boolean  test = keycloakService.deleteUser()
    ```
##### More instructions
To custom more feature you can create your service and inject some variable, example:
* Step 1: Inject RealmResource to your class:
```java
    @Autowired
    private RealmResource keycloakRealmResource;
```
* Step 2: Use RealResource
```java
  public class MyKeycloakService{
    @Autowired
    private RealmResource keycloakRealmResource;

    private String getCurrentUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SimpleKeycloakAccount details = (SimpleKeycloakAccount) authentication.getDetails();
        return details.getPrincipal().getName();
    }

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
  }
```



Build
-------
* Build with Unittest
```shell script
mvn clean install
```

* Build without Unittest
```shell script
mvn clean install -DskipTests
```

Contribute
-------
Please refer [Contributors Guide](CONTRIBUTING.md)

License
-------
This code is under the [MIT License](https://opensource.org/licenses/MIT).

See the [LICENSE](LICENSE) file for required notices and attributions.
