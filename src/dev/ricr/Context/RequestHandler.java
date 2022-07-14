package dev.ricr.Context;

import dev.ricr.Exceptions.NotValidHttpRequestException;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

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
    try {
      in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

      String firstLine = in.readLine();
      if (isHttpRequest(firstLine)) {
        request = new Request(client, in, firstLine);
        response = new Response(client, out);
      } else {
//        throw new NotValidHttpRequestException("Not a valid http request.");
        client.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }


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

  private boolean isHttpRequest (String firstLine) {
    // to be http request it must have a route method (GET, POST, DELETE, PUT, PATCH) and be HTTP/1.0 or HTTP/1.1
    // this is always the first line of a request payload
    if (firstLine == null) return false;

    String[] firstLineParts = firstLine.split(" ");
    String[] protocols = {"HTTP/1.1", "HTTP/1.0"};

    for (Methods method : Methods.values()) {
      if (method.toString().equals(firstLineParts[0]) && Arrays.asList(protocols).contains(firstLineParts[2])) {
        return true;
      }
    }

    return false;
  }

}
