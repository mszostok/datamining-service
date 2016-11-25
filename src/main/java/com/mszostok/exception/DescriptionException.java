package com.mszostok.exception;

public class DescriptionException extends RuntimeException {
  public DescriptionException(final String message) {
    super(message);
  }

  public DescriptionException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
