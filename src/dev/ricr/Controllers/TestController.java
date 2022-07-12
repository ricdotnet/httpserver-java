package dev.ricr.Controllers;

import com.google.gson.Gson;
import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Annotations.Post;
import dev.ricr.Context.Request;
import dev.ricr.Context.Response;
import dev.ricr.Services.TestService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller(path = "/test")
public class TestController {

  TestService testService;

  public TestController(TestService testService) {
    this.testService = testService;
  }

//  @Get(path = "/some/{id}/{username}")
//  public void somethingPost (Request request, Response response) {
//
//    String username = request.getParam("username");
//    String id = request.getParam("id");
//
//    Map<String, Object> resMap = new LinkedHashMap<>();
//    resMap.put("id", id);
//    resMap.put("username", username);
//
//    Gson res = new Gson();
//
//    response
//        .setStatus(200)
//        .setBody(res.toJson(resMap))
//        .send();
//  }

  @Get(path = "/some/{id}/{username}")
  public void somethingElse(Request request, Response response) {
    response.send();
  }

}
