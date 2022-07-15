package dev.ricr.Router;

import dev.ricr.Context.Request;
import dev.ricr.Context.Response;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class StaticRoute {

  private final String path;
  private final String dir;
  private final StaticRoute instance;

  public StaticRoute (String path, String dir) {
    this.path = path;
    this.dir = dir;
    this.instance = this;
  }

  // still need to inject both request and response objects at compile time
  public void serveStatic (Request request, Response response) {

    String[] routeParts = request.getRoute().split("/");
    StringBuilder route = new StringBuilder();
    if (routeParts.length == 2) {
      route.append("/").append("index.html");
    } else {
      for (int i = 2; i < routeParts.length; i++) {
        route.append("/").append(routeParts[i]);
      }
    }

    try {
      File file = new File(dir + route);

      BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
      if (!basicFileAttributes.isRegularFile()) {
        throw new RuntimeException();
      }

      String mimeType = Files.probeContentType(Path.of(String.valueOf(file)));
      if (mimeType == null) {
        String suffix = String.valueOf(file).substring(String.valueOf(file).lastIndexOf(".") + 1).toLowerCase();

        switch (suffix) {
          case "js":
            mimeType = "application/javascript; charset=utf-8";
            break;
          case "css":
            mimeType = "text/css; charset=utf-8";
            break;
          case "json":
            mimeType = "text/json; charset=utf-8";
            break;
        }
      }

      PrintWriter writer = response.getPrintWriter();
      BufferedOutputStream bufWriter = response.getBufferedOutputStream();

      writer.println("HTTP/1.1 200 OK");
      writer.println("Content-Type: " + mimeType);
      writer.println("Content-Length: " + basicFileAttributes.size());
      writer.println();
      writer.flush();
      bufWriter.write(readFileData(file, (int) basicFileAttributes.size()));
      bufWriter.flush();

    } catch (IOException e) {
      e.printStackTrace();
    }

    response.end();
  }

  private byte[] readFileData (File file, int fileLength) throws IOException {
    FileInputStream fileIn = null;
    byte[] fileData = new byte[fileLength];

    try {
      fileIn = new FileInputStream(file);
      fileIn.read(fileData);
    } finally {
      if (fileIn != null)
        fileIn.close();
    }

    return fileData;
  }

  public StaticRoute getInstance () {
    return this.instance;
  }
}
