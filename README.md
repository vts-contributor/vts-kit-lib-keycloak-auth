Keycloak Auth Library for Spring Boot
-------
This library provides utilities that make it easy to integrate Keycloak into spring boot project

<b>Feature List</b>
* Secure REST API
  * [Authentication](#authentication)
  * [Authorization](#authorization)
* Admin Operation
  * [Create New User](#create-new-user)
  * [Update User](#update-user)
  * [Delete User](#delete-user)
  * [Grant User Token](#grant-user-token)
  * [Get User By Token](#get-user-by-token)
  * [Logout Token](#logout-token)
  * [Logout All Session By User](#logout-all-session-by-user)
  * [Realm Resource Management](#realm-resource-management)

Quick start
-------
* Just add the dependency to an existing Spring Boot project
```xml
<dependency>
    <groupId>com.atviettelsolutions</groupId>
    <artifactId>vts-kit-lib-keycloak-auth</artifactId>
    <version>1.0.0</version>
</dependency>
```

* Then, add the following properties to your `application-*.yml` file.
```yaml

keycloak:
    realm: keycloak-demo # realm name
    auth-server-url: http://localhost:8080 #url connect to server keycloak
    resource: client-keycloak  #client id
    client-key-password: fI4B6eDBtwEi0NJsJMmYNSiIgj3jVp28 # client secret
```

* Declare `KeycloakService` to use some features if needed
```java
private KeycloakService keycloakService;

@Autowired
public void setKeycloakService(KeycloakService keycloakService) {
    this.keycloakService = keycloakService;
}
```


Usage
-------
### Securing REST API
#### Authentication

By default, all API will be authenticated. To ignore by path pattern, configuration list of `ignore` in your `application-*.yml`:
```yaml

keycloak:
  ignore: ['/auth/**', '/create'] #list of ignored url
  enabled: false #turn off keycloak spring boot auto-configuration
```

Get Current Authentication Information

```java
Authentication authentication = keycloakService.getCurrentAuthentication();
```
##### Handle Authentication
###### Get user login
```java
String userName = keycloakService.getUserLogin(Authentication authentication);
```
###### Get idUser login
```java
String idUser = keycloakService.getUserLoginId(Authentication authentication);
```
###### Get string token
```java
String token = keycloakService.getStringToken(Authentication authentication);
```
###### Get role 
```java
Set<String> role = keycloakService.getRoleId(Authentication authentication);
```
###### Get client id 
```java
String clientId = keycloakService.getClientId(Authentication authentication);
```
#### Authorization
To authorize your API: 
* <b>Option 1</b>: Define in your `application-*.yml` security constraint:
  ```yaml
  vtskit:
    keycloak:
      securityConstraints:
        - authRoles: [user] # list role will be accepted
          securityCollections:
            - name: API
              patterns: ['/test/*'] #list pattern url user have role in authRoles will be accessed
  ```
* <b>Option 2</b>: You can use annotation: 
    ```java
    @GetMapping(path = "test")
    @RolesAllowed({"user"})
    public ResponseEntity test(){
      return ResponseEntity.ok("test");
    }
    ```


### Admin Operation 
Add the following properties to your `application-*.yml` file.
```yaml 
keycloak:
    admin-realm: master # Admin realm
    admin-client-id: admin-cli # admin client id
    admin-username: admin # admin username
    admin-password: admin # admin password
    enabled: false #turn off keycloak spring boot auto-configuration
```
#### Create New User
```java
UserRepresentation user = new UserRepresentation();
user.setEmail("email@demo.com.vn");
user.setUsername("email@demo.com.vn");
CredentialRepresentation password = new CredentialRepresentation();
password.setType(CredentialRepresentation.PASSWORD);
password.setValue("password");
user.setCredentials(Arrays.asList(password));
keycloakService.createNewUser(user);
```

#### Get User by username
```java
UserRepresentation user = keycloakService.getUserByUsername(String username);
```
#### Activate User
```java
UserRepresentation user = keycloakService.getUserByUsername(String username);
user.setEnabled(true);
keycloakService.updateUser(user);
```

#### Update User
```java
UserRepresentation user = new UserRepresentation();
user.setId("b596c1b3-97ff-4ddf-90ec-13ad9a18f289");
user.setFirstName("Name");
keycloakService.updateUser(user);
```

#### Delete User
```java
String userId = "b596c1b3-97ff-4ddf-90ec-13ad9a18f289";
keycloakService.deleteUser(userId);
```

#### Grant User Token
```java
String username = "test";
String password = "test";
AccessTokenResponse token  = keycloakService.obtainAccessToken(username, password);
```
#### Refresh User Token
```java
AccessTokenResponse token  = keycloakService.refreshToken(refreshToken);
```

#### Get User By Token
```java
String token = "<token>";
UserRepresentation userRepresentation = keycloakService.getUserByToken(token);
```

#### Logout Token
```java
String token = "<token>";
keycloakService.logoutToken(token);
```

#### Logout All Session By User
```java
String userId = "b596c1b3-97ff-4ddf-90ec-13ad9a18f289";
keycloakService.logoutByUserId(userId);
```

#### Create Realm Role
```java
keycloakService.createRealmRole(String reaRoleName);
```
#### Set Role
```java
keycloakService.setRole(String userId, String roleName);
```
#### Get Role
```java
keycloakService.getRoleUser(String userId);
```
#### Realm Resource Management
To custom more feature you can create your service and inject some variable, example:
* Step 1: Inject RealmResource to your class:
    ```java
    private RealmResource realmResource;
    
    @Autowired
    public void setRealmResource(RealmResource realmResource) {
        this.realmResource = realmResource;
    }
    ```
* Step 2: Use RealResource
    ```java
    String userId= "user-id";
    CredentialRepresentation passwordCred = new CredentialRepresentation();
    passwordCred.setTemporary(false);
    passwordCred.setType(CredentialRepresentation.PASSWORD);
    passwordCred.setValue(password);
    UsersResource usersResource = realmResource.users();
    UserResource userResource = usersResource.get(userId);
    userResource.resetPassword(passwordCred);
    ```

Contribute
-------
#### Setting up the development environment
* <b>IDE:</b> Eclipse, Intellij IDEA
* <b>JDK:</b> >= JDK 8
* <b>Maven:</b> >= 3.6.0
* <b>Build:</b>
```shell script
mvn clean install
# Skip Unittest
mvn clean install -DskipTests
```
#### Contribute Guidelines
If you have any ideas, just open an issue and tell us what you think.

If you'd like to contribute, please refer [Contributors Guide](CONTRIBUTING.md)

License
-------
This code is under the [MIT License](https://opensource.org/licenses/MIT).

See the [LICENSE](LICENSE) file for required notices and attributions.
