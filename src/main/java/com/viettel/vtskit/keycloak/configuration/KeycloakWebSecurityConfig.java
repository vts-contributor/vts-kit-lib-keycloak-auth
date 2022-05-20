package com.viettel.vtskit.keycloak.configuration;

import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.keycloak.adapters.springsecurity.filter.KeycloakAuthenticationProcessingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
public class KeycloakWebSecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    @Autowired
    KeycloakProperties keycloakProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        super.configure(http);
        List ignore = keycloakProperties.getIgnore();
        if(!ignore.isEmpty()){

            ignore.forEach(e->{
                try {
                    http.authorizeRequests()
                            .antMatchers(e.toString()).permitAll();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
            http.authorizeRequests()
                    .antMatchers("/**").authenticated().anyRequest().permitAll();
            http.csrf().disable();
        }else{
            super.configure(http);
            http.authorizeRequests()
                    .antMatchers("/").authenticated()
                    .anyRequest().permitAll();
            http.csrf().disable();
        }
    }

    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider
                = keycloakAuthenticationProvider();
        SimpleAuthorityMapper simpleAuthorityMapper=new SimpleAuthorityMapper();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(
                simpleAuthorityMapper);
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
        KeycloakAuthenticationProcessingFilter filter =
                new KeycloakAuthenticationProcessingFilter(this.authenticationManagerBean());
        filter.setSessionAuthenticationStrategy(this.sessionAuthenticationStrategy());
        filter.setAuthenticationFailureHandler(new CustomKeycloakAuthenticationFailureHandler());
        return filter;
    }
}