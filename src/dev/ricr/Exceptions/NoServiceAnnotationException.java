package dev.ricr.Exceptions;

public class NoServiceAnnotationException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public NoServiceAnnotationException(String message) {
    super(message);
  }
}
