package dev.ricr;

import dev.ricr.Request.RequestHandler;
import dev.ricr.Router.Router;

import java.net.Socket;

/**
 * Just a test class to test the request connection
 * Needs to be better abstracted
 */
public class Connections extends Thread {
  Socket client;

  RequestHandler requestHandler;
  Router router = new Router();

  public Connections (Socket connection) {
    client = connection;

    // register the request
    requestHandler = new RequestHandler(connection);

    if (!router.findRoute(requestHandler.getRequest().getRoute())) {
      requestHandler
          .getResponse()
          .setStatus(400)
          .setBody("{\"message\": \"could not find route\"}")
          .send();

      return;
    }

    requestHandler.getResponse().setStatus(200).setBody("hello").send();

    // send the response
//    requestHandler.getResponse().send();
  }

}
