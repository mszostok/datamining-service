package com.mszostok.exception;

public class SubmissionException extends RuntimeException {
  public SubmissionException(final String message) {
    super(message);
  }

  public SubmissionException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
