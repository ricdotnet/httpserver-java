package dev.ricr.Router;

import java.util.ArrayList;
import java.util.List;

public class Controller {

  public List<Route> routes = new ArrayList<>();
  String route;

  public Controller (String route) {
    this.route = route;
  }

  public String getRoute () {
    return this.route;
  }

  public List<Route> getRoutes () {
    return this.routes;
  }


}
