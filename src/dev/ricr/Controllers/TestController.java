package dev.ricr.Controllers;

import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Post;
import dev.ricr.Request.Request;
import dev.ricr.Request.Response;
import dev.ricr.Services.TestService;

@Controller(path = "/test")
public class TestController {

  TestService testService;

  public TestController(TestService testService) {
    this.testService = testService;
  }

  @Post(path = "/some/{username}")
  public void somethingPost (Request request, Response response) {

    String username = request.getParam("username");
    System.out.println(request.getBody());

    response
        .setStatus(200)
        .setBody("{\"message\": \""+ username + " smells\"}")
        .send();
  }

}
