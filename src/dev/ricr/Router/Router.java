package dev.ricr.Router;

import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Annotations.Inject;
import dev.ricr.Exceptions.NoControllerAnnotationException;
import dev.ricr.Interfaces.IRouter;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Router implements IRouter {

  private final HashMap<String, ArrayList<Route<Object>>> routes = new HashMap<String, ArrayList<Route<Object>>>();

  private Router router;

  public Router () {
    this.buildRoutes();
  }

  @Override
  public void buildRoutes () {

    try {
      Reflections reflections = new Reflections("dev.ricr");
      Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Controller.class);

      for (Class<?> c : classes) {
        if (!c.isAnnotationPresent(Controller.class)) {
          throw new NoControllerAnnotationException(c.getName() + " is not defined with the @Controller() Annotation.");
        }

        String path = c.getAnnotation(Controller.class).path();
        routes.put(path, new ArrayList<>());

        ArrayList<Route<Object>> children = routes.get(path);
        for (Method method : c.getMethods()) {
          if (!method.isAnnotationPresent(Get.class)) continue;

          String child = method.getAnnotation(Get.class).path();
          children.add(new Route<>("GET", child, method, c.newInstance()));
        }
      }
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public HashMap<String, ArrayList<Route<Object>>> getRoutes () {
    return routes;
  }

  public void findRoute (String method, String path) {
    String[] segments = path.split("/", 3);
    String parent = "/" + segments[1];
    String children = "";
    if (segments.length > 2) {
      children = "/" + segments[2];
    }

    ArrayList<Route<Object>> route;
    if ((route = routes.get(parent)) != null) {
      System.out.println("There is a parent route....");

      for (Route<Object> rr : route) {
        if (rr.getPath().equals(children)) {
          System.out.println("Found a child route.");
          try {
            rr.getMethod().invoke(rr.getController());
          } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
          }
        }
      }

      System.out.println("But there is nothing to return...");
    }
  }

  public Router getRouter () {
    if (router == null) {
      this.router = new Router();
    }
    return router;
  }
}
