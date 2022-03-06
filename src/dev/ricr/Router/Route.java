package dev.ricr.Router;

public class Route {

  String method;
  String route;

  public Route get (String route) {
    this.method = "GET";
    this.route = route;

    return this;
  }

  public Route post (String route) {
    this.method = "POST";
    this.route = route;

    return this;
  }

  public String getRoute () {
    return this.route;
  }
}
