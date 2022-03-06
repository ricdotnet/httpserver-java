package dev.ricr.Request;

import java.io.*;
import java.net.Socket;

public class RequestHandler {

  Request request;
  Response response;

  BufferedReader in;
  BufferedWriter out;

  /**
   * Initiate the request and set the input stream and output stream
   *
   * @param client .
   */
  public RequestHandler (Socket client) {
    System.out.println("New request in....");
    try {
      in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
    } catch (IOException e) {
      e.printStackTrace();
    }

    request = new Request(client, in);
    response = new Response(client, out);
  }

  // TODO: These two methods below can be even more abstracted to allow for better request and response access
  /**
   * Allow access to the request object
   *
   * @return Request
   */
  public Request getRequest () {
    return this.request;
  }

  /**
   * Allow access to the response object
   *
   * @return Response
   */
  public Response getResponse () {
    return this.response;
  }

}
