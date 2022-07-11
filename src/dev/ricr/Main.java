package dev.ricr;

import dev.ricr.Annotations.Inject;
import dev.ricr.Container.Container;
import dev.ricr.Request.RequestHandler;
import dev.ricr.Router.Router;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;

public class Main {

  ServerSocket ss;
  Connections c;

  public void init () {
    try {
      ss = new ServerSocket(4000);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void connections (Router router) {
    try {
      while (true) {
        this.c = new Connections(ss.accept(), router);
        this.c.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main (String[] args) {
    Main main = new Main();
    main.init();
    Router router = new Router();
    System.out.println("Server is on and listening....");
    main.connections(router);
  }
}
