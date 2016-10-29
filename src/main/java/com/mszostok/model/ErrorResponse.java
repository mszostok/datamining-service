package com.mszostok.model;

import org.springframework.http.HttpStatus;

public final class ErrorResponse {
  private final HttpStatus status;

  private final String message;

  protected ErrorResponse(final String message, final HttpStatus status) {
    this.message = message;
    this.status = status;
  }

  public static ErrorResponse of(final String message, final HttpStatus status) {
    return new ErrorResponse(message, status);
  }

  public Integer getStatus() {
    return status.value();
  }

  public String getMessage() {
    return message;
  }
}
