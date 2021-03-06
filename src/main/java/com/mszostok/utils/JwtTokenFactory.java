package com.mszostok.utils;

import com.google.common.base.Strings;
import com.mszostok.configuration.JwtConfig;
import com.mszostok.model.UserCtx;
import com.mszostok.model.token.AccessJwtToken;
import com.mszostok.model.token.JwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public final class JwtTokenFactory {

  @Autowired
  private JwtConfig settings;

  public JwtToken createAccessToken(final UserCtx userContext) {
    if (Strings.isNullOrEmpty(userContext.getUsername())) {
      throw new IllegalArgumentException("Cannot create JWT Token without username");
    }

    if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty()) {
      throw new IllegalArgumentException("User doesn't have any privileges");
    }

    Claims claims = Jwts.claims().setSubject(userContext.getUsername());
    claims.put("scopes", userContext.getAuthorities().stream().map(Object::toString).collect(Collectors.toList()));
    DateTime currentTime = new DateTime();

    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuer(settings.getTokenIssuer())
        .setIssuedAt(currentTime.toDate())
        .setExpiration(currentTime.plusMinutes(settings.getTokenExpirationTime()).toDate())
        .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
        .compact();

    return new AccessJwtToken(token, claims);
  }

  public JwtToken createRefreshToken(final UserCtx userContext) {
    if (Strings.isNullOrEmpty(userContext.getUsername())) {
      throw new IllegalArgumentException("Cannot create JWT Token without username");
    }

    DateTime currentTime = new DateTime();

    Claims claims = Jwts.claims().setSubject(userContext.getUsername());
    claims.put("scopes", Collections.singletonList("ROLE_REFRESH_TOKEN"));

    String token = Jwts.builder()
        .setClaims(claims)
        .setIssuer(settings.getTokenIssuer())
        .setId(UUID.randomUUID().toString())
        .setIssuedAt(currentTime.toDate())
        .setExpiration(currentTime.plusMinutes(settings.getRefreshTokenExpTime()).toDate())
        .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
        .compact();

    return new AccessJwtToken(token, claims);
  }
}
