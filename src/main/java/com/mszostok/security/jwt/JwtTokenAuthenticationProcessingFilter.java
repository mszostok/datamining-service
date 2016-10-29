package com.mszostok.security.jwt;

import com.mszostok.configuration.SecurityConfig;
import com.mszostok.model.token.RawAccessJwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class JwtTokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
  @Autowired
  private AuthenticationFailureHandler failureHandler;
  @Autowired
  private JwtHeaderTokenExtractor tokenHeaderExtractor;

  public JwtTokenAuthenticationProcessingFilter(final RequestMatcher requiresAuthenticationRequestMatcher) {
    super(requiresAuthenticationRequestMatcher);
  }

  @Override
  public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {
    String tokenPayload = request.getHeader(SecurityConfig.JWT_TOKEN_HEADER_PARAM);
    RawAccessJwtToken token = new RawAccessJwtToken(tokenHeaderExtractor.extract(tokenPayload));

    return getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));
  }

  @Override
  protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain,
                                          final Authentication authResult) throws IOException, ServletException {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authResult);
    SecurityContextHolder.setContext(context);

    chain.doFilter(request, response);
  }

  @Override
  protected void unsuccessfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                            final AuthenticationException failed) throws IOException, ServletException {
    SecurityContextHolder.clearContext();
    failureHandler.onAuthenticationFailure(request, response, failed);
  }
}
