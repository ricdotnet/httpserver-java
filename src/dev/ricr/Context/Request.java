package dev.ricr.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Request {

  private String method;
  private String route;
  private String body;
  private final HashMap<String, String> headers = new HashMap<>();
  private final HashMap<String, String> params = new HashMap<>();
  private final HashMap<String, String> queries = new HashMap<>();

  private Socket client;
  private BufferedReader in;

  public Request (Socket client, BufferedReader in, String firstLine) {
    try {
      this.client = client;
      this.in = in;

      this.prepareMethodAndRoute(firstLine);
      this.processInputStream(in);
    } catch (NullPointerException e) {
      System.out.println("something broke?");
    }
  }

  private void prepareMethodAndRoute (String firstLine) {
    this.method = firstLine.split(" ")[0].trim();
    this.route = firstLine.split(" ")[1].trim();
  }

  /**
   * Get the request method
   *
   * @return The current request method verb.
   */
  public String getMethod () {
    return this.method;
  }

  /**
   * Get the request route
   *
   * @return The current visited route.
   */
  public String getRoute () {
    return this.route;
  }

  /**
   * Get a single header
   *
   * @param header Header string to search for.
   * @return A single header from the list of headers.
   */
  public String getHeader (String header) {
    return headers.get(header.toLowerCase());
  }

  public HashMap<String, String> getHeaders () {
    return this.headers;
  }

  /**
   * @param key   Key of the value to add on the params list.
   * @param value Value of the param to add on the list.
   */
  public void setParam (String key, String value) {
    this.params.put(key, value);
  }

  /**
   * @param key Key of the param to retrieve from the list of params.
   */
  public String getParam (String key) {
    return this.params.get(key);
  }

  public void setQuery (String key, String value) {
    this.queries.put(key, value);
  }

  public String getQuery(String key) {
    return this.queries.get(key);
  }

  public void setBody (String body) {
    this.body = body;
  }

  public String getBody () {
    return this.body;
  }

  // Todo: refactor
  private void processInputStream (BufferedReader in) {
    String content;
    List<String> lines = new ArrayList<>();
    try {
      while ((content = in.readLine()) != null) {
        lines.add(content);
        if (content.isEmpty()) {
          break;
        }
      }
      for (String line : lines) {
        if (!line.isEmpty()) {
          String[] header = line.split(":", 2);
          headers.put(header[0].trim().toLowerCase(), header[1].trim());
        }
      }

      if (getHeader("content-length") != null) {
        int contentLength = Integer.parseInt(getHeader("content-length"));
        StringBuilder body = new StringBuilder();
        try {
          if (contentLength > 0) {
            while (in.ready()) {
              body.append((char) in.read());
            }
            this.setBody(body.toString());
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } catch (
        IOException e) {
      e.printStackTrace();
    }
  }

  public Socket getClient () {
    return this.client;
  }

  public BufferedReader getReader () {
    return this.in;
  }

}
