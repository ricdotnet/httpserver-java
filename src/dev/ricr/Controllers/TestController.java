package dev.ricr.Controllers;

import com.google.gson.Gson;
import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Context.Request;
import dev.ricr.Context.Response;
import dev.ricr.Middlewares.AnotherMiddleware;
import dev.ricr.Middlewares.TestMiddleware;

import java.util.HashMap;
import java.util.Map;

@Controller(path = "/test")
public class TestController {

  @Get(path = "/get", middlewares = {TestMiddleware.class, AnotherMiddleware.class})
  public void test (Request request, Response response) {

    Map<String, String> body = new HashMap<>();
    body.put("name", "Ricardo");
    body.put("surname", "Rocha");

    Gson gson = new Gson();

    response.setStatus(200).setBody(gson.toJson(body)).send();
  }

  @Get(path = "/get2")
  public void test2 (Request request, Response response) {

    response.setStatus(200).setBody("hello world").send();
  }

}
