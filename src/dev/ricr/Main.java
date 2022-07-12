package dev.ricr;

import dev.ricr.Router.Router;
import java.io.IOException;
import java.net.ServerSocket;

public class Main {

  ServerSocket ss;

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
        Connections connections = new Connections(ss.accept(), router);
        new Thread(connections).start();
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
