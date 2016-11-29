package com.mszostok.exception;

public class IllegalArgException extends RuntimeException {
  public IllegalArgException(final String message) {
    super(message);
  }

  public IllegalArgException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
