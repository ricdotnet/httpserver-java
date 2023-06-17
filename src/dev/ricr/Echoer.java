package dev.ricr;

import dev.ricr.Configurations.EchoerConfigurations;
import dev.ricr.Router.Router;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Echoer extends Thread {

  private ServerSocket serverSocket;
  private final Router router;

  public Echoer() {
    try {
      serverSocket = new ServerSocket(EchoerConfigurations.APP_PORT);
    } catch (IOException e) {
      e.printStackTrace();
    }
    router = Router.init();
    System.out.println("Server is on and listening on port: " + EchoerConfigurations.APP_PORT);
  }

  @Override
  public void run() {
    try {
      while (serverSocket.isBound() && !serverSocket.isClosed()) {
        Socket socket = serverSocket.accept();
        EchoerConnection connection = new EchoerConnection(socket, router);
        connection.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
