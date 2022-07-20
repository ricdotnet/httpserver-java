package dev.ricr;

import dev.ricr.Configurations.EchoerConfigurations;
import dev.ricr.Container.Container;
import dev.ricr.Router.Router;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Echoer {

  ServerSocket ss;
  final List<HttpConnection> httpConnections = new LinkedList<>();

  public Echoer() {
    init();

    Router.init();
//    Router.addStatic("/assets", "./files");

    connections(Router.getRouter());

    System.out.println("Server is on and listening on port: " + EchoerConfigurations.APP_PORT);
  }

  public void init () {
    try {
      ss = new ServerSocket(EchoerConfigurations.APP_PORT);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void connections (Router router) {
    try {
      Container.addInstance(Thread.currentThread().getName(), Thread.currentThread());
      ExecutorService executorService = Executors.newFixedThreadPool(50);
      while (true) {
        HttpConnection connection = new HttpConnection(ss.accept(), router);
        this.httpConnections.add(connection);

        synchronized (httpConnections) {
          for (HttpConnection c : httpConnections) {
            executorService.execute(c);
            httpConnections.remove(c);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main (String[] args) {
    new Echoer();
  }
}
