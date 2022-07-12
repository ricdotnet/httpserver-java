package dev.ricr.Context;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class Response {

  Socket client;
  BufferedWriter out;
  StringBuilder body;

  int statusCode = 200; // 200 by default
  int contentLength = 0; // 0 by default

  public Response (Socket client, BufferedWriter out) {
    this.client = client;
    this.out = out;
  }

  /**
   * Set any headers
   *
   * @return Response
   */
  public Response setHeaders () {

    return this;
  }

  /**
   * Set the response status code
   *
   * @param statusCode .
   * @return Response
   */
  public Response setStatus (int statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  /**
   * Set the response body
   *
   * @param body .
   * @return Response
   */
  public Response setBody (String body) {
    this.body = new StringBuilder(); // reset the StringBuilder on every request
    this.body.append(body);
    this.contentLength = this.body.length();
    return this;
  }

  /**
   * Send the response and close the request socket
   */
  public void send () {
    try {
      out.write("HTTP/1.1 " + this.statusCode + " OK\r\n");
      out.write("Content-Type: application/json\r\n");
      out.write("Content-Length: " + this.contentLength + "\r\n");
      out.write("\r\n");
      out.write((body.toString().isEmpty()) ? "{}" : body.toString()); // if the body is empty then send two braces
      out.flush();
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
