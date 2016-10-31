package com.mszostok.model;

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

  public UserCtx(final String username, final List<GrantedAuthority> authorities) {
    this.username = username;
    this.authorities = authorities;
  }

  public String getUsername() {
    return username;
  }

  public List<GrantedAuthority> getAuthorities() {
    return authorities;
  }
}
