package dev.ricr.Router;

import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Service;
import dev.ricr.Configurations.EchoerConfigurations;
import dev.ricr.Container.DIContainer;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedList;

// TODO: abstract the middleware runner... no need to have the same code 100x times.

public class Router implements IRouter {

  private final HashMap<String, ArrayList<Route<Object>>> routesMap = new HashMap<>();
  private final HashMap<String, StaticRoute> staticRoutes = new HashMap<>();

  private RequestHandler requestHandler;

  private Router() {
  }

  private static Router router;

  public static Router init() {
    router = new Router();
    router.prepareGlobalMiddlewares();
    router.buildRoutes();

    return router;
  }

  private void prepareGlobalMiddlewares() {
    for (Class<?> middlewareClass : EchoerConfigurations.globalMiddlewares) {
      try {
        Object middleware = middlewareClass.getConstructor().newInstance();
        DIContainer.getInstance().put(middleware.getClass().getName(), middleware);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void buildRoutes() {
    try {
      Reflections controllersReflection = new Reflections(EchoerConfigurations.CONTROLLERS_PACKAGE);
      Set<Class<?>> classes = controllersReflection.getTypesAnnotatedWith(Controller.class);
      Reflections servicesReflection = new Reflections(EchoerConfigurations.SERVICES_PACKAGE);
      Set<Class<?>> servicesSet = servicesReflection.getTypesAnnotatedWith(Service.class);

      for (Class<?> service : servicesSet) {
        DIContainer.getInstance().put(service.getName(), service.getDeclaredConstructor().newInstance());
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
          DIContainer.getInstance().put(clazz.getName(), clazz.getDeclaredConstructor().newInstance());
        } else {
          DIContainer.getInstance().put(clazz.getName(), clazz.getConstructor(params).newInstance(initiatedServices));
        }

        String path = clazz.getAnnotation(Controller.class).path();
        Class<?>[] controllerMiddlewares = clazz.getAnnotation(Controller.class).middlewares();
        routesMap.put(path, new ArrayList<>());

        ArrayList<Route<Object>> children = routesMap.get(path);
        for (Method method : clazz.getDeclaredMethods()) {

          if (method.getAnnotations().length == 0) continue;

          String[] methodTypeStrings = method.getAnnotations()[0].annotationType().getName().split("\\.");
          String routeMethod = methodTypeStrings[methodTypeStrings.length - 1].toUpperCase();

          if (!RouterUtils.isValidRouteMethod(routeMethod)) continue;

          Object c = DIContainer.getInstance().get(clazz.getName());

          String childPath = method.getAnnotations()[0].annotationType().getName();

          // TODO: add a way to register a middleware to a parent route
          // for now, I will add a controller middleware to all routes, so it runs on each route call.
          // why? because I did not code a good way of abstracting a parent / controller route 😅
          Class<?>[] middlewares = RouterUtils.getAnnotationMiddlewares(method, childPath);
          Class<?>[] mergedMiddlewares = Arrays.copyOf(controllerMiddlewares, middlewares.length + controllerMiddlewares.length);
          System.arraycopy(middlewares, 0, mergedMiddlewares, controllerMiddlewares.length, middlewares.length);

          String child = RouterUtils.getAnnotationPath(method, childPath);
          Route<Object> route = new Route<>(routeMethod, child, method, c);

          for (Class<?> mid : mergedMiddlewares) {
            Object midObj = mid.getConstructor().newInstance();
            DIContainer.getInstance().putIfAbsent(midObj.getClass().getName(), midObj);
            route.addMiddleware(midObj.getClass().getName());
          }

          children.add(route);
        }
      }
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  public HashMap<String, ArrayList<Route<Object>>> getRoutesMap() {
    return routesMap;
  }

  /**
   * Find the matched route from the router list.
   *
   * @param requestHandler The current request handler
   * @param method         The route method verb {@link Methods}.
   * @param path           The full route path.
   */
  public void findRoute(RequestHandler requestHandler, String method, String path) {
    String[] pathParts = path.split("/", 3);
    String parent = "/" + pathParts[1];

    this.requestHandler = requestHandler;
    Object[] context = {requestHandler.getRequest(), requestHandler.getResponse()};

    try {
      if (isStaticFile(parent, context)) return;
      if (findMatch(parent, pathParts[2], method, context)) return;
    } catch (IndexOutOfBoundsException e) {
      e.printStackTrace();
    }
    send404();
  }

  public static Router getRouter() {
    return router;
  }

  public static void addStatic(String path, String dir) {
    router.staticRoutes.put(path, new StaticRoute(path, dir));
  }

  private void serveStatic() {

  }

  private void send404() {
    this.requestHandler
            .getResponse()
            .setStatus(404)
            .setBody("{\"error\": 404, \"message\": \"page not found\"}")
            .send();
  }

  private boolean isStaticFile(String parent, Object[] routeHandlers) {
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

  private boolean findMatch(String parent, String path, String method, Object[] routeHandlers) {
    if (routesMap.get(parent) != null) {
      for (Route<Object> route : routesMap.get(parent)) {
        if (RouterUtils.routeMatches(route.getPath(), path) && route.getVerb().equals(method)) {
          try {

            // we need to isolate the request object
            Object[] requestObject = {routeHandlers[0]};

            // invoke any global middleware before the route
            for (Class<?> middlewareClass : EchoerConfigurations.globalMiddlewares) {
              Object middleware = DIContainer.getInstance().get(middlewareClass.getName());
              middleware.getClass().getMethod("run", Request.class).invoke(middleware, requestObject);
            }

            // grab route middlewares
            LinkedList<String> middlewares = route.getMiddlewares();
            for (String middleware : middlewares) {
              Object mid = DIContainer.getInstance().get(middleware);
              mid.getClass().getMethod("run", Request.class).invoke(mid, requestObject);
            }

            route.getMethod().invoke(route.getController(), routeHandlers);
            return true;
          } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return false;
  }
}
