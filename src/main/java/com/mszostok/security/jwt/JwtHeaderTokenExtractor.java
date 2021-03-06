package com.mszostok.security.jwt;

import com.google.common.base.Strings;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;


/**
 * JwtHeaderTokenExtractor is used to extract Authorization token from header.
 */
@Component
public class JwtHeaderTokenExtractor {
  public static final String HEADER_PREFIX = "Bearer ";

  public String extract(final String header) {
    if (Strings.isNullOrEmpty(header)) {
      throw new AuthenticationServiceException("Authorization header cannot be blank!");
    }

    if (header.length() < HEADER_PREFIX.length()) {
      throw new AuthenticationServiceException("Invalid authorization header size.");
    }

    if (!header.startsWith(HEADER_PREFIX)) {
      throw new AuthenticationServiceException("Invalid authorization header type, please prefix token with 'Bearer '.");
    }

    return header.substring(HEADER_PREFIX.length(), header.length());
  }
}
