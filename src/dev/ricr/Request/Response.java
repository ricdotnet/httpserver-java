package dev.ricr.Request;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class Response {

  Socket client;
  BufferedWriter out;
  StringBuilder body;

  int statusCode;
  int contentLength;

  public Response (Socket client, BufferedWriter out) {
    this.client = client;
    this.out = out;
  }

  public Response setHeaders () {

    return this;
  }

  public Response setStatus (int statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  public Response setBody (String body) {
    this.body = new StringBuilder(); // reset the StringBuilder on every request
    this.body.append(body);
    this.contentLength = this.body.length();
    return this;
  }

  public void send () {
    System.out.println("sending response?");
    try {
      out.write("HTTP/1.1 " + this.statusCode + " OK\r\n");
      out.write("Content-Type: application/json\r\n");
      out.write("Content-Length: " + this.contentLength + "\r\n");
      out.write("\r\n");
      out.write(body.toString());
      out.flush();
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
