package com.mszostok.security.ajax;

import com.mszostok.domain.User;
import com.mszostok.model.UserCtx;
import com.mszostok.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AjaxAuthenticationProvider implements AuthenticationProvider {
  @Autowired
  private BCryptPasswordEncoder encoder;
  @Autowired
  private UserService userService;

  @Override
  public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
    Assert.notNull(authentication, "No authentication data provided");

    String email = (String) authentication.getPrincipal();
    String password = (String) authentication.getCredentials();

    User user = userService.getActiveUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    if (!encoder.matches(password, user.getPassword())) {
      throw new BadCredentialsException("Authentication Failed. Wrong credentials was provided.");
    }

    if (user.getRoles().isEmpty()) {
      throw new InsufficientAuthenticationException("User has no roles assigned");
    }

    List<GrantedAuthority> authorities = user.getRoles().stream()
        .map(authority -> new SimpleGrantedAuthority(authority.getRole()))
        .collect(Collectors.toList());

    UserCtx userContext = new UserCtx(user.getEmail(), authorities);

    return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
  }

  @Override
  public boolean supports(final Class<?> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }
}
