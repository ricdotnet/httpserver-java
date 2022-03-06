package dev.ricr.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Headers {

  private static HashMap<String, String> headers;

  /**
   * Register all headers in a single hashmap
   */
  public static void registerHeaders (BufferedReader in) {
    headers = new HashMap<String, String>(); // reset the hashmap on every request?
    String content;
    List<String> lines = new ArrayList<String>();
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
          headers.put(header[0].trim(), header[1].trim());
        }
      }

      if (headers.get("Content-Length") != null) {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));

        StringBuilder body = new StringBuilder();
        if (contentLength > 0) {
          while (in.ready()) {
            body.append((char) in.read());
          }
        }

        System.out.println(body);
      }
    } catch (
        IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Get a single header from a key
   * eg: getHeaderValue("Content-Type");
   *
   * @param key header value
   * @return String
   */
  public static String getHeaderValue (String key) {
    return headers.get(key);
  }

  /**
   * Get all headers
   *
   * @return HashMap
   */
  public static HashMap<String, String> getHeaders () {
    return headers;
  }

}
