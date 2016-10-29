package com.mszostok.security.ajax;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AjaxLoginProcessingFilter responsibility is to process Ajax authentication requests by
 * deserialization and basic validation data from incoming JSON payload.
 */
@Slf4j
public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

  @Autowired
  private AjaxAwareAuthenticationSuccessHandler successHandler;
  @Autowired
  private AjaxAwareAuthenticationFailureHandler failureHandler;

  public AjaxLoginProcessingFilter(final String defaultProcessUrl) {
    super(defaultProcessUrl);
  }

  @Override
  public Authentication attemptAuthentication(final HttpServletRequest req, final HttpServletResponse resp)
      throws AuthenticationException, IOException, ServletException {

    if (!HttpMethod.POST.name().equals(req.getMethod())) {
      if (log.isDebugEnabled()) {
        log.debug("Authentication method not supported. Request method: " + req.getMethod());
      }
      throw new AuthenticationServiceException("Authentication method not supported");
    }

    String username = req.getParameter("username");
    String password = req.getParameter("password");

    if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password)) {
      throw new AuthenticationServiceException("Username or Password not provided");
    }

    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

    return this.getAuthenticationManager().authenticate(token);
  }

  @Override
  protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain,
                                          final Authentication authResult) throws IOException, ServletException {
    successHandler.onAuthenticationSuccess(request, response, authResult);
  }

  @Override
  protected void unsuccessfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                            final AuthenticationException failed) throws IOException, ServletException {
    SecurityContextHolder.clearContext();
    failureHandler.onAuthenticationFailure(request, response, failed);
  }
}
