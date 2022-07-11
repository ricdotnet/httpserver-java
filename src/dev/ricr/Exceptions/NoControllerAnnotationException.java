package dev.ricr.Exceptions;

public class NoControllerAnnotationException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public NoControllerAnnotationException(String message) {
    super(message);
  }
}
