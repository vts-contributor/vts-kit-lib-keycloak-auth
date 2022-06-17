package com.viettel.vtskit.keycloak.filter;

import com.viettel.vtskit.keycloak.configuration.KeycloakProperties;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.List;

public class KeycloakAuthFilter extends KeycloakAuthenticationProcessingFilter {

    public KeycloakAuthFilter(AuthenticationManager authenticationManager, KeycloakProperties keycloakProperties) {
        super(authenticationManager);
        List<String> ignore = keycloakProperties.getIgnore();
        if(ignore == null){
            ignore = new ArrayList<>();
        }
        /**
         * Auto add default spring error path
         */
        ignore.add("/error");
        List<AntPathRequestMatcher> requestMatchers = new ArrayList<>();
        for(String item : ignore){
            requestMatchers.add(new AntPathRequestMatcher(item));
        }
        setRequiresAuthenticationRequestMatcher(request -> {
            for(AntPathRequestMatcher matcher : requestMatchers){
                if(matcher.matches(request)){
                    return false;
                }
            }
            return true;
        });
    }

}
