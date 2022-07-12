package dev.ricr.Router;

import dev.ricr.Annotations.*;
import dev.ricr.Container.Container;
import dev.ricr.Context.Methods;
import dev.ricr.Context.RequestHandler;

import java.lang.reflect.Method;

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
   *
   * @param current
   *        Route taken from the router list.
   * @param visited
   *        Route that the user tried to visit.
   * @return A boolean value of whether the route is valid or not.
   */
  public static boolean routeMatches(String current, String visited) {
    String preparedRoute = current.replaceAll("([{][a-zA-Z\\d]+[}])", "([a-zA-Z0-9]+)");

    boolean matches = visited.matches(preparedRoute);

    if (matches) {
      saveRouteParams(current, visited);
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

  private static void saveRouteParams(String current, String visited) {
    String[] currentParts = current.split("/");
    String[] visitedParts = visited.split("/");

    RequestHandler requestHandler =
        (RequestHandler) Container.getInstance(RequestHandler.class.getName() + Thread.currentThread().getName());

    for (int i = 0; i < currentParts.length; i++) {
      if (currentParts[i].matches("([{][a-zA-Z\\d]+[}])")) {
        requestHandler.getRequest().setParam(currentParts[i].replace("{", "").replace("}", ""), visitedParts[i]);
      }
    }
  }

}
