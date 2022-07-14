package dev.ricr;

import dev.ricr.Annotations.Inject;
import dev.ricr.Container.*;
import dev.ricr.Context.RequestHandler;
import dev.ricr.Router.Router;

import java.net.Socket;
import java.net.SocketException;

/**
 * Just a test class to test the request connection
 * Needs to be better abstracted
 */
public class HttpConnection implements Runnable {
  Socket client;
  Router router;

  @Inject
  RequestHandler requestHandler;

  public HttpConnection (Socket connection, Router router) {
    try {
      this.client = connection;
      this.client.setTcpNoDelay(true);
    } catch (SocketException e) {
      e.printStackTrace();
    }
    this.router = router;
  }

  public void run () {
    // register the request
    requestHandler = new RequestHandler(this.client);
    Container.addInstance(RequestHandler.class.getName() + Thread.currentThread().getName(), requestHandler);

    // both request method and route need to match
    String requestMethod = requestHandler.getRequest().getMethod();
    String requestRoute = requestHandler.getRequest().getRoute();

    router.findRoute(requestMethod, requestRoute);

    // remove all request objects from the container
    Container.removeInstance(RequestHandler.class.getName() + Thread.currentThread().getName());
  }

}
