package com.mszostok.exception;

public class UploadException extends RuntimeException{
  public UploadException(final String message) {
    super(message);
  }

  public UploadException(final String message, final Throwable cause) {
    super(message, cause);
  }
}

