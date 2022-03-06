package dev.ricr.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class Request {

  Socket client;
  BufferedReader in;
  String method;
  String route;

  public Request (Socket client, BufferedReader in) {
    this.client = client;
    this.in = in;

    try {
      this.prepareMethodAndRoute(in.readLine().split("\n"));
      Headers.registerHeaders(in);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void prepareMethodAndRoute (String[] firstLine) {
    this.method = firstLine[0].split(" ")[0].trim();
    this.route = firstLine[0].split(" ")[1].trim();
  }

  /**
   * Get the request method
   *
   * @return String
   */
  public String getMethod () {
    return this.method;
  }

  /**
   * Get the request route
   *
   * @return String
   */
  public String getRoute () {
    return this.route;
  }

  /**
   * Get a single header
   *
   * @param header .
   * @return String
   */
  public String getHeader (String header) {
    return Headers.getHeaderValue(header);
  }

}
