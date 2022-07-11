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
public class Connections extends Thread {
  Socket client;

  @Inject
  RequestHandler requestHandler;

  public Connections (Socket connection, Router router) {
    client = connection;

    // register the request
    requestHandler = new RequestHandler(connection);
    Container.addInstance(requestHandler.getClass().getName(), requestHandler);

    // both request method and route need to match
    String requestMethod = requestHandler.getRequest().getMethod();
    String requestRoute = requestHandler.getRequest().getRoute();
    router.findRoute(requestMethod, requestRoute);

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

}
