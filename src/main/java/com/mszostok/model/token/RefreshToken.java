package com.mszostok.model.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public final class RefreshToken implements JwtToken {
  private Jws<Claims> claims;

  private RefreshToken(final Jws<Claims> claims) {
    this.claims = claims;
  }

  /**
   * Creates and validates Refresh token.
   *
   * @param token raw (unpacked) token
   * @param signingKey which will be used to sign token
   * @return refresh token
   */
  public static Optional<RefreshToken> create(final RawAccessJwtToken token, final String signingKey) {
    Jws<Claims> claims = token.parseClaims(signingKey);

    List<String> scopes = claims.getBody().get("scopes", List.class);
    if (scopes == null || scopes.isEmpty()
        || !scopes.stream().filter("ROLE_REFRESH_TOKEN"::equals).findFirst().isPresent()) {
      return Optional.empty();
    }

    return Optional.of(new RefreshToken(claims));
  }

  @Override
  public String getToken() {
    return null;
  }

  public Jws<Claims> getClaims() {
    return claims;
  }

  public String getJti() {
    return claims.getBody().getId();
  }

  public String getSubject() {
    return claims.getBody().getSubject();
  }
}
