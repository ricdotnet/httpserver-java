package dev.ricr.Controllers;

import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Container.Container;
import dev.ricr.Request.RequestHandler;

@Controller(path = "/test")
public class TestController  {

  @Get(path = "/test")
  public void hello() {

  }

  @Get(path = "/vedant")
  public void vedant() {
    RequestHandler requestHandler = (RequestHandler) Container.getInstance(RequestHandler.class.getName());
    requestHandler
        .getResponse()
        .setStatus(400)
        .setBody("{\"message\": \"vedant smells\"}")
        .send();
  }

}
