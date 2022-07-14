package dev.ricr.Exceptions;

public class NotValidHttpRequestException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NotValidHttpRequestException(String message) {
    super(message);
  }
}
