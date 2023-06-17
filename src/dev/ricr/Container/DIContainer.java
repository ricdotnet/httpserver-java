package dev.ricr.Container;

import java.util.HashMap;

public class DIContainer {
  private DIContainer() {
  }

  private static final HashMap<String, Object> container = new HashMap<>();

  public static HashMap<String, Object> getInstance() {
    return container;
  }

  public void put(String key, Object instance) {
    if (get(key) == null) {
      container.put(key, instance);
    }
  }

  public void remove(String key) {
    container.remove(key);
  }

  public Object get(String key) {
    return container.get(key);
  }

}
