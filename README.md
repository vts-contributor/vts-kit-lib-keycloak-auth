Keycloak Auth Library for Spring Boot
-------
This library provides utilities that make it easy to integrate Keycloak into spring boot project

Feature List:
* [Secure REST API with keycloak](#Securing REST API using Keycloak)
* [Admin Client](#Admin client)

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
    realm: keycloak-demo
    auth-server-url: http://localhost:8080 
    resource: client-keycloak 
    bearer-only: true
```

Usage
-------
##### Securing REST API using Keycloak

  * By default, all API will not be authenticated. 
  * If you only authenticate your API:
      ```java
      @GetMapping(path = "test")
      public ResponseEntity test(Authentication authentication){
        if(authentication.isAuthenticated()){
         //some code
        }
        return ResponseEntity.ok("test");
      }
      ```
  * To authenticate and authorize your API: 
    * You must define in your application.yml security constraint:
      ```yaml
      vtskit:
        keycloak:
          securityConstraints:
            - authRoles: [user]
              securityCollections:
                - name: API
                  patterns: ['/test/*']
      ```
    * Or you can use annotation: 
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
    
  * To get user login information:
    ```java
    @RestController
    @RequestMapping("/")
    public class TestController {
        @Autowired
        KeycloakService keycloakService;
      
        @GetMapping(path = "test")
        public ResponseEntity test(Authentication authentication){
                return ResponseEntity.ok(keycloakService.getUserInfo(authentication).getDetails());
        }
    }
    ```
##### Admin Client 
Next version 1.0.1

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
