package com.viettel.vtskit.keycloak.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthFailureHandler implements AuthenticationFailureHandler {

    private HandlerExceptionResolver exceptionResolver;

    public AuthFailureHandler(HandlerExceptionResolver exceptionResolver){
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        exceptionResolver.resolveException(request, response, null, exception);
    }
}
