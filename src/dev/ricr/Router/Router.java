package dev.ricr.Router;

import java.util.ArrayList;
import java.util.List;

public class Router {

  List<Route> routes = new ArrayList<Route>();

  public Router () {
    Route post = new Route().post("/post");
    routes.add(post);
    Route get = new Route().get("/get");
    routes.add(get);
  }

  public boolean findRoute (String r) {
    for (Route route : routes) {
      if (route.getRoute().equals(r)) {
        return true;
      }
    }

    return false;
  }

}
