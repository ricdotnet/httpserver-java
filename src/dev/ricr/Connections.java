package dev.ricr;

import dev.ricr.Annotations.Inject;
import dev.ricr.Container.*;
import dev.ricr.Request.RequestHandler;
import dev.ricr.Router.Router;

import java.net.Socket;

/**
 * Just a test class to test the request connection
 * Needs to be better abstracted
 */
public class Connections implements Runnable {
  Socket client;
  Router router;

  @Inject
  RequestHandler requestHandler;

  public Connections (Socket connection, Router router) {
    this.client = connection;
    this.router = router;

//    if (finalRoute == 0) {
//      requestHandler
//          .getResponse()
//          .setStatus(400)
//          .setBody("{\"message\": \"could not find route\"}")
//          .send();
//
//      return;
//    }

//    if (finalRoute == 2) {
//      requestHandler
//          .getResponse()
//          .setStatus(200)
//          .setBody("{\"message\": \"found top route with no subroutes\"}")
//          .send();
//
//      return;
//    }
//
//    requestHandler.getResponse().setStatus(200).setBody("hello").send();

  }

  public void run() {
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
