package dev.ricr.Controllers;

import dev.ricr.Router.Controller;
import dev.ricr.Router.Route;

// extending the Controller class we then call super() with the name of the top route
// to add a route simply routes.add(new Route<AnotherController>("method", "/route", this))
public class AnotherController extends Controller {

  public AnotherController () {
    super("/another");
    routes.add(new Route<>("GET", "/test", this));
    routes.add(new Route<>("POST", "/some", this));
  }

  public void test () {
    System.out.println("testing this?");
  }

  public void some() {
    System.out.println("hello there i guess?");
  }
}
