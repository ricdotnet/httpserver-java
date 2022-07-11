package dev.ricr.Controllers;

import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Container.Container;
import dev.ricr.Request.RequestHandler;

@Controller(path = "/another")
public class AnotherController {

  @Get(path = "/test")
  public void testMethod () {
    try {
      Thread.sleep(10000);
      RequestHandler requestHandler = (RequestHandler) Container.getInstance(RequestHandler.class.getName());
      requestHandler
          .getResponse()
          .setStatus(400)
          .setBody("{\"message\": \"test route\"}")
          .send();
    } catch (InterruptedException e) {
       // ignore
    }
  }

  @Get(path = "/some")
  public void someMethod () {
    System.out.println("hello there i guess?");
  }
}
