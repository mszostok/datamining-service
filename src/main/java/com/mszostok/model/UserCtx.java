package com.mszostok.model;

import com.google.common.base.Strings;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * Current logged user object with proper username and authorities list.
 *
 * @author mszostok
 */
public class UserCtx {
    private final String username;
    private final List<GrantedAuthority> authorities;

    private UserCtx(String username, List<GrantedAuthority> authorities) {
        this.username = username;
        this.authorities = authorities;
    }
    //TODO: constructor should be enough
    public static UserCtx create(String username, List<GrantedAuthority> authorities) {
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
