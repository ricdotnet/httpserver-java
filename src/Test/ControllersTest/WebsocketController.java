package Test.ControllersTest;

import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Context.Request;
import dev.ricr.Context.Response;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

// todo for ws use @Websocket
@Controller(path = "/connect")
public class WebsocketController {

  @Get(path = "/chat")
  public void chat (Request request, Response response) {
    System.out.println("Upgrade to websocket request.");


    try {
      OutputStream out = request.getClient().getOutputStream();
      var key = request.getHeader("sec-websocket-key") + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
      MessageDigest hash2 = MessageDigest.getInstance("SHA-1");
      hash2.update(key.getBytes(StandardCharsets.UTF_8));
      var hash = Base64.getEncoder().encodeToString(hash2.digest());

      response.getWriter()
          .write("HTTP/1.1 101 Switching Protocols\r\n" +
          "Upgrade: websocket\r\n" +
          "Connection: Upgrade\r\n" +
          "Sec-WebSocket-Accept: " + hash + "\r\n" +
          "\r\n");
      response.getWriter().flush();

      while (true) {
        byte[] resp = "hello world".getBytes();
        byte[] frames = new byte[2];
        frames[0] = (byte) 129;
        frames[1] = (byte) resp.length;
        int frameCount = 2;
        int bLength = frameCount + resp.length;
        byte[] reply = new byte[bLength];

        int bLim = 0;
        for(int i=0; i<frameCount;i++){
          reply[bLim] = frames[i];
          bLim++;
        }
        for(int i=0; i<resp.length;i++){
          reply[bLim] = resp[i];
          bLim++;
        }

        out.write(reply);
        while(true) {
          out.write(reply);
          Thread.sleep(1000);
        }
      }

    } catch (IOException | NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] encode (String mess) throws IOException {
    byte[] rawData = mess.getBytes();

    int frameCount = 0;
    byte[] frame = new byte[10];

    frame[0] = (byte) 129;

    if (rawData.length <= 125) {
      frame[1] = (byte) rawData.length;
      frameCount = 2;
    } else if (rawData.length >= 126 && rawData.length <= 65535) {
      frame[1] = (byte) 126;
      int len = rawData.length;
      frame[2] = (byte) ((len >> 8) & (byte) 255);
      frame[3] = (byte) (len & (byte) 255);
      frameCount = 4;
    } else {
      frame[1] = (byte) 127;
      int len = rawData.length;
      frame[2] = (byte) ((len >> 56) & (byte) 255);
      frame[3] = (byte) ((len >> 48) & (byte) 255);
      frame[4] = (byte) ((len >> 40) & (byte) 255);
      frame[5] = (byte) ((len >> 32) & (byte) 255);
      frame[6] = (byte) ((len >> 24) & (byte) 255);
      frame[7] = (byte) ((len >> 16) & (byte) 255);
      frame[8] = (byte) ((len >> 8) & (byte) 255);
      frame[9] = (byte) (len & (byte) 255);
      frameCount = 10;
    }

    int bLength = frameCount + rawData.length;

    byte[] reply = new byte[bLength];

    int bLim = 0;
    for (int i = 0; i < frameCount; i++) {
      reply[bLim] = frame[i];
      bLim++;
    }
    for (int i = 0; i < rawData.length; i++) {
      reply[bLim] = rawData[i];
      bLim++;
    }

    return reply;
  }

}
