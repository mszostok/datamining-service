package com.mszostok.utils;

import com.mszostok.model.UserCtx;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

  private SecurityUtils() {
  }

  public static Optional<String> getCurrentUserLogin() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    String userName = null;

    if (authentication != null) {
      if (authentication.getPrincipal() instanceof UserCtx) {
        UserCtx springSecurityUser = (UserCtx) authentication.getPrincipal();
        userName = springSecurityUser.getUsername();
      } else if (authentication.getPrincipal() instanceof String) {
        userName = (String) authentication.getPrincipal();
      }
    }
    return Optional.ofNullable(userName);
  }
}
