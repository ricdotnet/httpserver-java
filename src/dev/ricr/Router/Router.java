package dev.ricr.Router;

import dev.ricr.Controllers.AnotherController;
import dev.ricr.Controllers.TestController;
import dev.ricr.Interfaces.IRouter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Router implements IRouter {

  private final List<Controller> routes = new ArrayList<>();

  public Router () {
    this.setRoutes();
  }

  @Override
  public void setRoutes () {
    routes.add(new TestController());
    routes.add(new AnotherController());
  }

  public List<Controller> getRoutes () {
    return routes;
  }

  // need to refer to this method both the method and the route
  // we will need the method to then match with the sub route on the controller
  // the route gets split, we find the main route and then match with the sub route
  public int findRoute (String m, String r) {

    String[] paths = r.split("/");
    String controllerPath = "/" + paths[1]; // the path comes with a / at the beginning so index 0 will be empty

    for (Controller controller : routes) {
      if (controller.getRoute().equals(controllerPath)) {
        for (Route<?> route : controller.getRoutes()) {
          String subRoute = "/" + paths[2];
          if (route.getMethod().equals(m) && route.getRoute().equals(subRoute)) {
            String func = route.getRoute().split("/")[1];
            try {
              Method n = controller.getClass().getDeclaredMethod(func);
              n.invoke(controller);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
              e.printStackTrace();
            }
            return 1;
          }
        }

        return 2;
      }
    }

    return 0;
  }

}
