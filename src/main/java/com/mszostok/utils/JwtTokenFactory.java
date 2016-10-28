package com.mszostok.utils;

import com.google.common.base.Strings;
import com.mszostok.model.UserCtx;
import com.mszostok.model.token.AccessJwtToken;
import com.mszostok.model.token.JwtToken;
import com.mszostok.configuration.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtTokenFactory {
    private final JwtConfig settings;

    @Autowired
    public JwtTokenFactory(JwtConfig settings) {
        this.settings = settings;
    }

    //TODO: return interface, refresh token in refresh ??
    public AccessJwtToken createAccessJwtToken(UserCtx userContext) {
        if (Strings.isNullOrEmpty(userContext.getUsername()))
            throw new IllegalArgumentException("Cannot create JWT Token without username");

        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty()) 
            throw new IllegalArgumentException("User doesn't have any privileges");

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

    public JwtToken createRefreshToken(UserCtx userContext) {
        if (Strings.isNullOrEmpty(userContext.getUsername())) {
            throw new IllegalArgumentException("Cannot create JWT Token without username");
        }

        DateTime currentTime = new DateTime();

        Claims claims = Jwts.claims().setSubject(userContext.getUsername());
        claims.put("scopes", Arrays.asList("ROLE_REFRESH_TOKEN"));
        
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
