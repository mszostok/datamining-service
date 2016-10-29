package com.mszostok.security.ajax;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mszostok.exception.JwtTokenExpiredException;
import com.mszostok.model.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class AjaxAwareAuthenticationFailureHandler implements AuthenticationFailureHandler {
  @Autowired
  private ObjectMapper mapper;

  @Override
  public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
                                      final AuthenticationException ex) throws IOException, ServletException {

    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    //TODO: add for token bad credentials (Invalid token)
    if (ex instanceof BadCredentialsException) {
      mapper.writeValue(response.getWriter(), ErrorResponse.of("Invalid username or password", HttpStatus.UNAUTHORIZED));
    } else if (ex instanceof JwtTokenExpiredException) {
      mapper.writeValue(response.getWriter(), ErrorResponse.of("Token has expired", HttpStatus.UNAUTHORIZED));
    }
    //else if (e instanceof AuthMethodNotSupportedException) {
    //  mapper.writeValue(response.getWriter(), ErrorResponse.of(e.getMessage(), ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED));
    //}
    mapper.writeValue(response.getWriter(), ErrorResponse.of("Authentication failed", HttpStatus.UNAUTHORIZED));
  }
}
