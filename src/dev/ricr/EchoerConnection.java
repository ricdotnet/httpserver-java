package dev.ricr;

import dev.ricr.Context.RequestHandler;
import dev.ricr.Router.Router;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class EchoerConnection extends Thread {
  private Socket client;
  private final Router router;

  RequestHandler requestHandler;

  public EchoerConnection(Socket connection, Router router) {
    try {
      this.client = connection;
      this.client.setTcpNoDelay(true);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    this.router = router;
  }

  @Override
  public void run () {
    // register the request
    requestHandler = new RequestHandler(this.client);

    // both request method and route need to match
    if (!this.client.isClosed()) {
      String requestMethod = requestHandler.getRequest().getMethod();
      String requestRoute = requestHandler.getRequest().getRoute();

      router.findRoute(requestHandler, requestMethod, requestRoute);
    }
    // remove all request objects from the container
    try {
      this.client.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
