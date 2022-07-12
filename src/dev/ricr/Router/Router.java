package dev.ricr.Router;

import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Annotations.Service;
import dev.ricr.Container.Container;
import dev.ricr.Exceptions.NoControllerAnnotationException;
import dev.ricr.Exceptions.NoServiceAnnotationException;
import dev.ricr.Interfaces.IRouter;
import dev.ricr.Request.RequestHandler;
import dev.ricr.Services.TestService;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
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
      Set<Class<?>> servicesSet = reflections.getTypesAnnotatedWith(Service.class);

      for (Class<?> service : servicesSet) {
        Container.addInstance(service.getName(), service.getDeclaredConstructor().newInstance());
      }

      for (Class<?> clazz : classes) {
        if (!clazz.isAnnotationPresent(Controller.class)) {
          throw new NoControllerAnnotationException(clazz.getName() + " is not defined with the @Controller Annotation.");
        }

        Constructor<?>[] services = clazz.getConstructors();
        Class<?>[] params = services[0].getParameterTypes();
        Object[] initiatedServices = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
          try {
            Class<?> serviceClass = params[i];
            if (!serviceClass.isAnnotationPresent(Service.class)){
              throw new NoServiceAnnotationException(serviceClass.getName() + " is not defined with the @Service Annotation.");
            }

            initiatedServices[i] = serviceClass.getConstructor().newInstance();
          } catch (NoSuchMethodException e) {
            e.printStackTrace();
          }
        }

        if (params.length == 0) {
          Container.addInstance(clazz.getName(), clazz.getDeclaredConstructor().newInstance());
        } else {
          Container.addInstance(clazz.getName(), clazz.getConstructor(params).newInstance(initiatedServices));
        }

        String path = clazz.getAnnotation(Controller.class).path();
        routes.put(path, new ArrayList<>());

        ArrayList<Route<Object>> children = routes.get(path);
        for (Method method : clazz.getMethods()) {
          if (!method.isAnnotationPresent(Get.class)) continue;

          Object c = Container.getInstance(clazz.getName());

          String child = method.getAnnotation(Get.class).path();
          children.add(new Route<>("GET", child, method, c));
        }
      }
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
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

    System.out.println("Request to: " + path);
    ArrayList<Route<Object>> route;
    if ((route = routes.get(parent)) != null) {

      for (Route<Object> rr : route) {
        if (rr.getPath().equals(children)) {
          try {

            // this will inject the request handler into the method allowing us to then access it when writing a route
            RequestHandler requestHandler =
                (RequestHandler) Container.getInstance(RequestHandler.class.getName() + Thread.currentThread().getName());

            rr.getMethod().invoke(rr.getController(), requestHandler);
            return;
          } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
          }
        }
      }

      System.out.println("But there is nothing to return...");
    }

      send404();
    }

  public Router getRouter () {
    if (router == null) {
      this.router = new Router();
    }
    return router;
  }

  private void send404() {
    RequestHandler requestHandler =
        (RequestHandler) Container.getInstance(RequestHandler.class.getName() + Thread.currentThread().getName());
    requestHandler.getResponse().setStatus(200).setBody("{\"error\": 404, \"message\": \"page not found\"}").send();
  }
}
