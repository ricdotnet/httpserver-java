package dev.ricr.Controllers;

import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Request.RequestHandler;
import dev.ricr.Services.AnotherService;
import dev.ricr.Services.TestService;

@Controller(path = "/test")
public class TestController {

  TestService testService;
  AnotherService anotherService;

  public TestController(TestService testService, AnotherService anotherService) {
    this.testService = testService;
    this.anotherService = anotherService;
  }

  @Get(path = "/test")
  public void hello (RequestHandler requestHandler) {
  }

  @Get(path = "/some")
  public void something (RequestHandler requestHandler) {

    testService.serviceMethod();
    anotherService.someService();

    requestHandler
        .getResponse()
        .setStatus(200)
        .setBody("{\"message\": \"something responded\"}")
        .send();
  }

}
