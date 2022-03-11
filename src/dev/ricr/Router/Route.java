package dev.ricr.Router;

public class Route<T> {

  String method;
  String route;
  T controller;

  public Route (String method, String route, T controller) {
    this.method = method;
    this.route = route;
    this.controller = controller;
  }

  public String getMethod () {
    return this.method;
  }

  public String getRoute () {
    return this.route;
  }

  public Class getClassName () {
    try {
      return Class.forName(this.controller.getClass().getName());
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
