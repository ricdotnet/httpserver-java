package dev.ricr.Router;

import dev.ricr.Annotations.*;
import dev.ricr.Container.DIContainer;
import dev.ricr.Context.Methods;
import dev.ricr.Context.RequestHandler;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class RouterUtils {

  /**
   * When building the router check if the defined annotation is a valid method verb
   *
   * @param method The route method verb
   * @return A boolean value
   */
  public static boolean isValidRouteMethod (String method) {
    for (Methods m : Methods.values()) {
      if (m.name().equals(method.toUpperCase())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param current Route taken from the router list.
   * @param visited Route that the user tried to visit.
   * @return A boolean value of whether the route is valid or not.
   */
  public static boolean routeMatches (String current, String visited) {
    String preparedRoute = current.replaceAll("([{][a-zA-Z\\d]+[}])", "([a-zA-Z0-9]+)");
    String preparedVisited = "/" + removeTrailingSlash(URI.create(visited).getPath());

    String query = URI.create(visited).getQuery();
    if (query != null) {
      String[] queryPairs = query.split("&");
      if (queryPairs.length > 0) saveRouteQueryParams(queryPairs);
    }

    boolean matches = preparedVisited.matches(preparedRoute);

    if (matches) {
      saveRouteParams(current, preparedVisited);
    }

    return matches;
  }

  public static String getAnnotationPath (Method method, String annotationName) {
    try {
      Class<?> classType = Class.forName(annotationName);

      if (classType == Get.class) {
        return method.getAnnotation(Get.class).path();
      }

      if (classType == Post.class) {
        return method.getAnnotation(Post.class).path();
      }

      if (classType == Put.class) {
        return method.getAnnotation(Put.class).path();
      }

      if (classType == Patch.class) {
        return method.getAnnotation(Patch.class).path();
      }

      if (classType == Delete.class) {
        return method.getAnnotation(Delete.class).path();
      }

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static Class<?>[] getAnnotationMiddlewares (Method method, String annotationName) {
    try {
      Class<?> classType = Class.forName(annotationName);

      if (classType == Get.class) {
        return method.getAnnotation(Get.class).middlewares();
      }

      if (classType == Post.class) {
        return method.getAnnotation(Post.class).middlewares();
      }

      if (classType == Put.class) {
        return method.getAnnotation(Put.class).middlewares();
      }

      if (classType == Patch.class) {
        return method.getAnnotation(Patch.class).middlewares();
      }

      if (classType == Delete.class) {
        return method.getAnnotation(Delete.class).middlewares();
      }

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    return null;
  }

  private static void saveRouteParams (String current, String visited) {
    String[] currentParts = current.split("/");
    String[] visitedParts = visited.split("/");

    RequestHandler requestHandler =
        (RequestHandler) DIContainer.getInstance().get(RequestHandler.class.getName() + Thread.currentThread().getName());

    for (int i = 0; i < currentParts.length; i++) {
      if (currentParts[i].matches("([{][a-zA-Z\\d]+[}])")) {
        requestHandler.getRequest().setParam(currentParts[i].replace("{", "").replace("}", ""), visitedParts[i]);
      }
    }
  }

  private static void saveRouteQueryParams (String[] queryPairs) {
    RequestHandler requestHandler =
        (RequestHandler) DIContainer.getInstance().get(RequestHandler.class.getName() + Thread.currentThread().getName());

    for (String pair : queryPairs) {
      String[] keyValue = pair.split("=");
      requestHandler.getRequest().setQuery(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
    }
  }

  private static String removeTrailingSlash (String pathParts) {
    if (pathParts.charAt(pathParts.length() - 1) == '/') {
      return pathParts.substring(0, pathParts.length() - 1);
    }

    return pathParts;
  }

}
