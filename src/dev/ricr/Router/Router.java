package dev.ricr.Router;

import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Service;
import dev.ricr.Configurations.EchoerConfigurations;
import dev.ricr.Container.Container;
import dev.ricr.Context.Request;
import dev.ricr.Context.Response;
import dev.ricr.Exceptions.NoControllerAnnotationException;
import dev.ricr.Exceptions.NoServiceAnnotationException;
import dev.ricr.Interfaces.IRouter;
import dev.ricr.Context.Methods;
import dev.ricr.Context.RequestHandler;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Router implements IRouter {

  private final HashMap<String, ArrayList<Route<Object>>> routesMap = new HashMap<>();
  private final HashMap<String, StaticRoute> staticRoutes = new HashMap<>();

  private Router router;

  public Router () {
    this.buildRoutes();
  }

  @Override
  public void buildRoutes () {
    try {
      Reflections controllersReflection = new Reflections(EchoerConfigurations.CONTROLLERS_PACKAGE);
      Set<Class<?>> classes = controllersReflection.getTypesAnnotatedWith(Controller.class);
      Reflections servicesReflection = new Reflections(EchoerConfigurations.SERVICES_PACKAGE);
      Set<Class<?>> servicesSet = servicesReflection.getTypesAnnotatedWith(Service.class);

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
            if (!serviceClass.isAnnotationPresent(Service.class)) {
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
        routesMap.put(path, new ArrayList<>());

        ArrayList<Route<Object>> children = routesMap.get(path);
        for (Method method : clazz.getDeclaredMethods()) {

          if (method.getAnnotations().length == 0) continue;

          String[] methodTypeStrings = method.getAnnotations()[0].annotationType().getName().split("\\.");
          String routeMethod = methodTypeStrings[methodTypeStrings.length - 1].toUpperCase();

          if (!RouterUtils.isValidRouteMethod(routeMethod)) continue;

          Object c = Container.getInstance(clazz.getName());

          String child = RouterUtils.getAnnotationPath(method, method.getAnnotations()[0].annotationType().getName());
          children.add(new Route<>(routeMethod, child, method, c));
        }
      }
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  public HashMap<String, ArrayList<Route<Object>>> getRoutesMap () {
    return routesMap;
  }

  /**
   * Find the matched route from the router list.
   *
   * @param method The route method verb {@link Methods}.
   * @param path   The full route path.
   */
  public void findRoute (String method, String path) {
    String[] pathParts = path.split("/", 3);
    String parent = "/" + pathParts[1];

    // this will inject the request handler into the method allowing us to then access it when writing a route
    RequestHandler requestHandler =
        (RequestHandler) Container.getInstance(RequestHandler.class.getName() + Thread.currentThread().getName());

    Object[] routeHandlers = {requestHandler.getRequest(), requestHandler.getResponse()};

    try {
      if (isStaticFile(parent, routeHandlers)) return;
      if (findMatch(parent, pathParts[2], method, routeHandlers)) return;
    } catch (IndexOutOfBoundsException e) {
      // ignore to send a 404
    }

    send404();
  }

  public Router getRouter () {
    if (router == null) {
      this.router = new Router();
    }
    return router;
  }

  public void addStatic (String path, String dir) {
    this.staticRoutes.put(path, new StaticRoute(path, dir));
  }

  private void serveStatic () {

  }

  private void send404 () {
    RequestHandler requestHandler =
        (RequestHandler) Container.getInstance(RequestHandler.class.getName() + Thread.currentThread().getName());
    requestHandler.getResponse().setStatus(404).setBody("{\"error\": 404, \"message\": \"page not found\"}").send();
  }

  private boolean isStaticFile (String parent, Object[] routeHandlers) {
    if (staticRoutes.get(parent) != null) {
      StaticRoute staticRoute = staticRoutes.get(parent).getInstance();
      try {
        Method m = staticRoute.getClass().getDeclaredMethod("serveStatic", Request.class, Response.class);
        m.invoke(staticRoute, routeHandlers);
      } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
        e.printStackTrace();
      }
      return true;
    }
    return false;
  }

  private boolean findMatch (String parent, String path, String method, Object[] routeHandlers) {
    if (routesMap.get(parent) != null) {
      for (Route<Object> route : routesMap.get(parent)) {
        if (RouterUtils.routeMatches(route.getPath(), path) && route.getVerb().equals(method)) {
          try {
            route.getMethod().invoke(route.getController(), routeHandlers);
            return true;
          } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return false;
  }
}
