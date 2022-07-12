package dev.ricr.Controllers;

import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Container.Container;
import dev.ricr.Request.RequestHandler;

@Controller(path = "/another")
public class AnotherController {

  public AnotherController() {}

  @Get(path = "/test")
  public void testMethod (RequestHandler requestHandler) {
    try {
      Thread.sleep(10000);
      requestHandler
          .getResponse()
          .setStatus(200)
          .setBody("{\"message\": \"test route\"}")
          .send();

      Thread.currentThread().interrupt();
    } catch (InterruptedException e) {
       // ignore
    }
  }

  @Get(path = "/some")
  public void someMethod () {
    System.out.println("hello there i guess?");
  }
}
