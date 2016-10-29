package com.mszostok.security.ajax;

import com.mszostok.domain.User;
import com.mszostok.model.UserCtx;
import com.mszostok.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Verify user credentials against database.
 * If username and password do not match the record in the database authentication exception is thrown
 * Create UserCtx and populate it with username and user privileges
 * Upon successful authentication delegate creation of JWT Token to AjaxAwareAuthenticationSuccessHandler
 */
@Component
public class AjaxAuthenticationProvider implements AuthenticationProvider {
  //TODO: autowire in my onw
  private final BCryptPasswordEncoder encoder;
  private final UserService userService;

  @Autowired
  public AjaxAuthenticationProvider(final UserService userService, final BCryptPasswordEncoder encoder) {
    this.userService = userService;
    this.encoder = encoder;
  }

  @Override
  public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
    Assert.notNull(authentication, "No authentication data provided");

    String username = (String) authentication.getPrincipal();
    String password = (String) authentication.getCredentials();

    //TODO: username -> email
    User user = userService.getActiveUserByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    if (!encoder.matches(password, user.getPassword())) {
      throw new BadCredentialsException("Authentication Failed. Wrong credentials was provided.");
    }

    if (user.getRoles().isEmpty()) {
      throw new InsufficientAuthenticationException("User has no roles assigned");
    }

    List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getRole()))
        .collect(Collectors.toList());

    UserCtx userContext = UserCtx.create(user.getEmail(), authorities);

    return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
  }

  @Override
  public boolean supports(final Class<?> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }
}
