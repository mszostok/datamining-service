package com.mszostok.exception;

public class CompetitionException extends RuntimeException {
  public CompetitionException(final String message) {
    super(message);
  }

  public CompetitionException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
