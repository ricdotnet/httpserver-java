package Test.Controllers;

import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Context.Request;
import dev.ricr.Context.Response;
import dev.ricr.Router.Router;

@Controller(path = "/api")
public class HelloWorld {

  @Get(path = "/hello")
  public void Hello(Request request, Response response) {
    response.setStatus(200).setBody("hello world").send();
  }

  @Get(path = "/world")
  public void World(Request request, Response response) throws InterruptedException {
    Thread.sleep(10000);
    response.setBody("OK").send();
  }

}
