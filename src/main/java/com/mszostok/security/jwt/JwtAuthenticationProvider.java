package com.mszostok.security.jwt;

import com.mszostok.configuration.JwtConfig;
import com.mszostok.model.UserCtx;
import com.mszostok.model.token.RawAccessJwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@SuppressWarnings("unchecked")
public final class JwtAuthenticationProvider implements AuthenticationProvider {
  private final JwtConfig jwtConfig;

  @Autowired
  public JwtAuthenticationProvider(final JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  @Override
  public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
    RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();

    Jws<Claims> jwsClaims = rawAccessToken.parseClaims(jwtConfig.getTokenSigningKey());
    String subject = jwsClaims.getBody().getSubject();
    List<String> scopes = jwsClaims.getBody().get("scopes", List.class);
    List<GrantedAuthority> authorities = scopes.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    UserCtx context = new UserCtx(subject, authorities);

    return new JwtAuthenticationToken(context, context.getAuthorities());
  }

  @Override
  public boolean supports(final Class<?> authentication) {
    return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
  }
}
