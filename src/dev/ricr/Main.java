package dev.ricr;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {

  ServerSocket ss;

  public void init () {
    try {
      ss = new ServerSocket(4000);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void connections () {
    try {
      while (true) {
        Connections c = new Connections(ss.accept());
        c.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main (String[] args) {
    Main main = new Main();
    main.init();
    System.out.println("Server is on and listening....");
    main.connections();
  }
}
