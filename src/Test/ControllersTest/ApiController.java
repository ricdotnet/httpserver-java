//package dev.ricr.Controllers;
//
//import dev.ricr.Annotations.Controller;
//import dev.ricr.Annotations.Get;
//import dev.ricr.Context.Request;
//import dev.ricr.Context.Response;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//
//@Controller(path = "/api")
//public class ApiController {
//
//  @Get(path = "/v1")
//  public void apiV1 (Request request, Response response) {
//    try {
//      File file = new File("./api/src/index.html");
//
//      BufferedOutputStream bufOut = new BufferedOutputStream(request.getClient().getOutputStream());
//      response.getWriter()
//          .write("HTTP/1.1 200 OK\r\n" +
//              "Content-Type: text/html\r\n" +
//              "Content-Length: " + file.length() + "\r\n" +
//              "\r\n");
//      response.getWriter().flush();
//      bufOut.write(readFileData(file, (int) file.length(), 0, (int) file.length()));
//      bufOut.flush();
//
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }
//
//  private byte[] readFileData (File file, int fileLength, int i, long length) throws IOException {
//    FileInputStream fileIn = null;
//    byte[] fileData = new byte[fileLength];
//
//    try {
//      fileIn = new FileInputStream(file);
//      fileIn.read(fileData);
//    } finally {
//      if (fileIn != null)
//        fileIn.close();
//    }
//
//    return fileData;
//  }
//
////  private void serveIndex() {
////    try {
////      FileInputStream file = new FileInputStream("/api/index.html");
////      response.setResponseCode(200, "OK");
////      response.addHeader("Content-Type", "text/html");
////      StringBuffer buf = new StringBuffer();
////      // TODO this is slow
////      int c;
////      while ((c = file.read()) != -1) {
////        buf.append((char) c);
////      }
////      response.addBody(buf.toString());
////    } catch (FileNotFoundException e) {
////      response.setResponseCode(404, "Not Found");
////    } catch (IOException e) {
////      throw new RuntimeException(e);
////    }
////  }
//
//}
