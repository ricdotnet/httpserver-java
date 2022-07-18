package dev.ricr.Context;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;

// TODO: refactor this class
public class Request {

  private static int BUF_SIZE = 8192;
  //  private static int MAX_REQ_SIZE = 512;
  private static int MAX_REQ_SIZE = 1000000;
  private int readLen;
  private String method;
  private String route;
  private String body;
  private final HashMap<String, String> formBody = new HashMap<>();
  private final HashMap<String, String> headers = new HashMap<>();
  private final HashMap<String, String> params = new HashMap<>();
  private final HashMap<String, String> queries = new HashMap<>();

  private Socket client;
  private BufferedInputStream in;

  public Request (Socket client, BufferedInputStream in, String firstLine) {
    try {
      this.client = client;
      this.in = in;

      this.prepareMethodAndRoute(firstLine);
      this.processIncomingRequest(this.in);
//      this.processInputStream(in);
    } catch (NullPointerException e) {
//      System.out.println("something broke?");
      e.printStackTrace();
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
    final String[] hh = new String[1];
    headers.forEach((key, value) -> {
      if (header.equalsIgnoreCase(key)) {
        hh[0] = value;
      }
    });
    if (hh[0] == null) return null;
    return hh[0];
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

  public String getQuery (String key) {
    return this.queries.get(key);
  }

  public void setBody (String body) {
    this.body = body;
  }

  public String getBody (String... key) {
    if (key == null || key.length == 0) {
      return this.body;
    }
//    if (key.length == 1 && this.formBody.size() == 0) {
//      return null;
//    }
    return this.formBody.get(key[0]);
  }

  private void processIncomingRequest (BufferedInputStream in) {
    // get the first 8192 bytes
    // apache header limit is 8KB but other web servers have different sizes (tomcat is over 40kb)
    // nginx is 4-8kb
    byte[] buf = new byte[Request.BUF_SIZE];
    in.mark(Request.BUF_SIZE);

    int read = -1;
    int splitByte = 0;

    try {
      read = in.read(buf, 0, Request.BUF_SIZE);
      while (read > 0) {
        this.readLen += read;
        splitByte = findHeaderEnd(buf, this.readLen);
        if (splitByte > 0) {
          break;
        }
        read = in.read(buf, this.readLen, Request.BUF_SIZE - this.readLen);
      }

      if (splitByte < this.readLen) {
        this.in.reset();
        this.in.skip(splitByte);
      }

      BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf, 0, this.readLen)));
      processHeaders(reader);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void processHeaders (BufferedReader reader) {

    try {
      while (reader.ready()) {
        String line = reader.readLine();
        if (line.length() == 0) break;
        String[] headerParts = line.split(": ");
        if (headerParts.length == 2) {
          headers.put(headerParts[0], headerParts[1]);
        }
      }

      // this should be used for both formurlencoded and multipart
      String contentType = getHeader("content-type");
      if ("application/x-www-form-urlencoded".equals(contentType) || contentType.contains("multipart/form-data")) {
        processBody(contentType);
      }

      if ("application/json".equals(contentType)) {
        processJsonBody(reader);
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // TODO: if the user uploads a file then the 512 limit will be to large for the memory so
  // this should also be handled by writing the buffer to a temp file
  private void processBody (String contentType) {
    long bodySize = Long.parseLong(this.getHeader("content-length"));

    ByteArrayOutputStream baos = null;
    DataOutput requestDataOutput = null;
    RandomAccessFile randomAccessFile = null;

    try {
      if (bodySize < MAX_REQ_SIZE) {
        baos = new ByteArrayOutputStream();
        requestDataOutput = new DataOutputStream(baos);
      } else {
        randomAccessFile = new RandomAccessFile("./files/tmp", "rw");
        requestDataOutput = randomAccessFile;
      }

      byte[] buf = new byte[MAX_REQ_SIZE];
      while (this.readLen >= 0 && bodySize > 0) {
        this.readLen = in.read(buf, 0, (int) Math.min(bodySize, MAX_REQ_SIZE));
        bodySize -= this.readLen;
        if (this.readLen > 0) {
          requestDataOutput.write(buf, 0, this.readLen);
        }
      }

      ByteBuffer bbuf;
      if (baos != null) {
        bbuf = ByteBuffer.wrap(baos.toByteArray(), 0, baos.size());
      } else {
        bbuf = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, randomAccessFile.length());
        randomAccessFile.seek(0);
      }

      byte[] postBytes = new byte[bbuf.remaining()];
      bbuf.get(postBytes);
      String line = new String(postBytes);

      if ("application/x-www-form-urlencoded".equals(contentType)) {
        processFormUrlEncoded(URLDecoder.decode(line, StandardCharsets.UTF_8));
      } else {

        // TODO: Refactor and abstract. this works hardcoded but we dont want that.
        // we want the app to be all auto and save files for us

        String boundary = "--" + getHeader("content-type").substring("multipart/form-data; boundary=".length());

        byte[] multipart = new byte[MAX_REQ_SIZE];
        multipart = bbuf.array();

        Object[] starts = findBoundaryStarts(multipart, boundary.getBytes());
        // file boundary starts at 159 ... 132
//        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(multipart, 159 + 125, multipart.length - 159 - 130 - (boundary + "--").length())));
        bbuf.position(159 + 132);
        int len = (multipart.length - 159) - (157);
        byte[] fbuf = new byte[len];
        bbuf.get(fbuf);
        FileOutputStream fileOutputStream = new FileOutputStream("./files/image.jpeg");
        fileOutputStream.write(fbuf);
        fileOutputStream.flush();

      }

    } catch (IOException e) {
      // ignore
    }
  }

  private void processFormUrlEncoded (String body) {
    int contentLength = Integer.parseInt(getHeader("content-length"));
    if (contentLength > 0) {
      for (String kv : body.split("&")) {
        String[] c = kv.split("=");
        if (c.length < 2) continue; // if someone submits some form field with only key (name) lets ignore that field
        formBody.put(c[0], c[1]);
      }
    }
  }

  private void processJsonBody (BufferedReader reader) {
    try {
      StringBuilder bodyBuilder = new StringBuilder();
      while (reader.ready()) {
        bodyBuilder.append(reader.readLine());
      }
      this.body = String.valueOf(bodyBuilder);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // referred from nanohttpd (https://github.com/NanoHttpd/nanohttpd)
  private int findHeaderEnd (final byte[] buf, int rlen) {
    int splitbyte = 0;
    while (splitbyte + 1 < rlen) {
      // RFC2616
      if (buf[splitbyte] == '\r' && buf[splitbyte + 1] == '\n' && splitbyte + 3 < rlen && buf[splitbyte + 2] == '\r' && buf[splitbyte + 3] == '\n') {
        return splitbyte + 4;
      }

      // tolerance
      if (buf[splitbyte] == '\n' && buf[splitbyte + 1] == '\n') {
        return splitbyte + 2;
      }
      splitbyte++;
    }
    return 0;
  }

  private Object[] findBoundaryStarts (byte[] req, byte[] boundary) {
    ArrayList<Integer> starts = new ArrayList<>();
    for (int i = 0; i < req.length - boundary.length + 1; ++i) {
      boolean found = true;
      for (int j = 0; j < boundary.length; ++j) {
        if (req[i + j] != boundary[j]) {
          found = false;
          break;
        }
      }
      if (found) starts.add(i);
    }

    System.out.println(starts);
    return starts.toArray();
  }

  public Socket getClient () {
    return this.client;
  }

  public BufferedInputStream getReader () {
    return this.in;
  }

}
