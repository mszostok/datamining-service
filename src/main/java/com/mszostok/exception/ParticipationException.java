package com.mszostok.exception;

public class ParticipationException extends RuntimeException {
  public ParticipationException(final String message) {
    super(message);
  }

  public ParticipationException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
