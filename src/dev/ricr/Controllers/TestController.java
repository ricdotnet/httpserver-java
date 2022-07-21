package dev.ricr.Controllers;

import com.google.gson.Gson;
import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Context.Request;
import dev.ricr.Context.Response;
import dev.ricr.Middlewares.AnotherMiddleware;
import dev.ricr.Middlewares.ControllerMiddleware;
import dev.ricr.Middlewares.TestMiddleware;

import java.util.HashMap;
import java.util.Map;

@Controller(path = "/test", middlewares = {ControllerMiddleware.class})
public class TestController {

  @Get(path = "/get", middlewares = {TestMiddleware.class, AnotherMiddleware.class})
  public void test (Request request, Response response) {

    Map<String, String> body = new HashMap<>();
    body.put("name", "Ricardo");
    body.put("surname", "Rocha");

    Gson gson = new Gson();

    response.setStatus(200).setBody(gson.toJson(body)).send();
  }

  @Get(path = "/get2/{username}")
  public void test2 (Request request, Response response) {

    Map<String, String> body = new HashMap<>();
    body.put("username1", request.getData("username").toString());
    body.put("username2", request.getData("username", String.class));

    Gson gson = new Gson();

    response.setStatus(200).setBody(gson.toJson(body)).send();
  }

}
