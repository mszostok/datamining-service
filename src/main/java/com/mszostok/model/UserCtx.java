package com.mszostok.model;

import com.google.common.base.Strings;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.List;

/**
 * Current logged user object with proper username and authorities list.
 */
public final class UserCtx implements Serializable {
  private static final long serialVersionUID = 7085655808462921276L;

  private final String username;
  private final List<GrantedAuthority> authorities;

  // TODO: constructor should be enough
  private UserCtx(final String username, final List<GrantedAuthority> authorities) {
    this.username = username;
    this.authorities = authorities;
  }


  /**
   * Factory for user context.
   *
   * @param username    is user login
   * @param authorities which user should have
   * @return user context
   */
  public static UserCtx create(final String username, final List<GrantedAuthority> authorities) {
    if (Strings.isNullOrEmpty(username)) {
      throw new IllegalArgumentException("Username is blank: " + username);
    }
    return new UserCtx(username, authorities);
  }

  public String getUsername() {
    return username;
  }

  public List<GrantedAuthority> getAuthorities() {
    return authorities;
  }
}
