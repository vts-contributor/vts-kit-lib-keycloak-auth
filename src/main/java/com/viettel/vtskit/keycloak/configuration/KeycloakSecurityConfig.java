package com.viettel.vtskit.keycloak.configuration;

import com.viettel.vtskit.keycloak.filter.KeycloakAuthFilter;
import com.viettel.vtskit.keycloak.handler.AuthFailureHandler;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class KeycloakSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    private KeycloakProperties keycloakProperties;
    private HandlerExceptionResolver exceptionResolver;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        List<String> ignore = keycloakProperties.getIgnore();
        if(!CollectionUtils.isEmpty(ignore)){
            http.authorizeRequests().antMatchers(ignore.stream().toArray(String[]::new)).permitAll();
        }
        http.authorizeRequests().anyRequest().authenticated();
        http.csrf().disable();
    }

    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    /**
     * Defines the session authentication strategy.
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new NullAuthenticatedSessionStrategy();
    }

    @Override
    protected KeycloakAuthenticationProcessingFilter keycloakAuthenticationProcessingFilter() throws Exception {
        KeycloakAuthFilter filter = new KeycloakAuthFilter(authenticationManagerBean(), keycloakProperties);
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy());
        filter.setAuthenticationFailureHandler(new AuthFailureHandler(exceptionResolver));
        return filter;
    }

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public void setKeycloakProperties(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Qualifier("handlerExceptionResolver")
    public void setExceptionResolver(HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }
}
