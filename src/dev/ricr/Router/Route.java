package dev.ricr.Router;

import java.lang.reflect.Method;

public class Route<T> {

  private final String verb;
  private final String path;
  private final Method method;
  private final T controller;

  public Route (String verb, String path, Method method, T controller) {
    this.verb = verb;
    this.path = path;
    this.method = method;
    this.controller = controller;
  }

  public String getVerb () {
    return this.verb;
  }

  public String getPath () {
    return this.path;
  }

  public Method getMethod () {
    return this.method;
  }

  public T getController() {
    return this.controller;
  }
}