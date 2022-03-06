package dev.ricr.Request;

import java.io.*;
import java.net.Socket;

public class RequestHandler {

  Request request;
  Response response;

  BufferedReader in;
  BufferedWriter out;

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

  public Request getRequest () {
    return this.request;
  }

  public Response getResponse () {
    return this.response;
  }

}
