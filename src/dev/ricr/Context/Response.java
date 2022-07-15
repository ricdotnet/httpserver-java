package dev.ricr.Context;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class Response {

  private final Socket client;
  private final BufferedWriter out;
  private BufferedOutputStream bufferedOutputStream;
  private PrintWriter printWriter;
  private StringBuilder body = new StringBuilder();
  private final HashMap<String, String> responseHeaders = new HashMap<>();

  private int statusCode = 200; // 200 by default
  private int contentLength = 0; // 0 by default

  public Response (Socket client, BufferedWriter out) {
    this.client = client;
    this.out = out;

    try {
      this.printWriter = new PrintWriter(out);
      this.bufferedOutputStream = new BufferedOutputStream(client.getOutputStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Set any headers
   *
   * @return Response
   */
  public Response setHeader (String key, String value) {
    this.responseHeaders.put(key, value);
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
      printWriter.println("HTTP/1.1 " + this.statusCode + " OK");

      // build headers
      printWriter.println("Access-Control-Allow-Headers: x-prototype-version,x-requested-with");
      printWriter.println("Access-Control-Allow-Methods: GET,POST");
      printWriter.println("Access-Control-Allow-Origin: *");
      for (String header : responseHeaders.keySet()) {
        printWriter.println(header + ": " + responseHeaders.get(header));
      }
      printWriter.println("content-length: " + body.length());

      printWriter.println();
      printWriter.println((body == null) ? "{}" : body);
      printWriter.flush();
  }

  public void end () {
    try {
      out.flush();
      client.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public BufferedWriter getWriter () {
    return this.out;
  }

  public PrintWriter getPrintWriter () {
    return this.printWriter;
  }

  public BufferedOutputStream getBufferedOutputStream () {
    return this.bufferedOutputStream;
  }

}
