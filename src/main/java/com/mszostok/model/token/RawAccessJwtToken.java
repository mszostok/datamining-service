package com.mszostok.model.token;

import com.mszostok.exception.JwtTokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.Serializable;

@Slf4j
public final class RawAccessJwtToken implements JwtToken, Serializable {
  private static final long serialVersionUID = -5588236597529660888L;

  private String token;

  public RawAccessJwtToken(final String token) {
    this.token = token;
  }

  /**
   * Parses and validates JWT Token signature.
   *
   * @param signingKey which was used to sign token
   * @return jws claims saved in token
   * @throws BadCredentialsException  when token is invalid
   * @throws JwtTokenExpiredException when token was expired
   */
  public Jws<Claims> parseClaims(final String signingKey) {
    try {
      return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.token);
    } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
      log.error("Invalid JWT Token", ex);
      throw new BadCredentialsException("Invalid JWT token: ", ex);
    } catch (ExpiredJwtException expiredEx) {
      log.info("JWT Token is expired", expiredEx);
      throw new JwtTokenExpiredException(this, "JWT Token expired", expiredEx);
    }
  }

  @Override
  public String getToken() {
    return token;
  }
}
