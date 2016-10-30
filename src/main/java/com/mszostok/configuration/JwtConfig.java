package com.mszostok.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Jwt configuration class, all properties are configurable by env variables.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
public class JwtConfig {

  private Integer tokenExpirationTime;

  private String tokenIssuer;

  private String tokenSigningKey;

  private Integer refreshTokenExpTime;
}
