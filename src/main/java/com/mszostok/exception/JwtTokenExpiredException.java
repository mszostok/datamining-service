package com.mszostok.exception;

import com.mszostok.model.token.JwtToken;
import org.springframework.security.core.AuthenticationException;


public final class JwtTokenExpiredException extends AuthenticationException {
  private static final long serialVersionUID = -5959543783324224864L;

  private JwtToken token;

  public JwtTokenExpiredException(final String msg) {
    super(msg);
  }

  public JwtTokenExpiredException(final JwtToken token, final String msg, final Throwable throwable) {
    super(msg, throwable);
    this.token = token;
  }

  public String token() {
    return this.token.getToken();
  }
}
