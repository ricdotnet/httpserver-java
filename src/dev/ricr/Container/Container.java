package dev.ricr.Container;

import java.util.HashMap;

public class Container {

  private static final HashMap<String, Object> container = new HashMap<>();

  public static HashMap<String, Object> getContainer () {
    return container;
  }

  public static void addInstance (String key, Object instance) {
    if (getInstance(key) == null) {
      container.put(key, instance);
    }
  }

  public static void removeInstance(String key) {
    container.remove(key);
  }

  public static Object getInstance (String key) {
    return container.get(key);
  }

}
