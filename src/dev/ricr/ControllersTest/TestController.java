package dev.ricr.ControllersTest;

import com.google.gson.Gson;
import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Annotations.Post;
import dev.ricr.Context.Request;
import dev.ricr.Context.Response;
import dev.ricr.Services.TestService;

import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller(path = "/test")
public class TestController {

  TestService testService;

  public TestController(TestService testService) {
    this.testService = testService;
  }

  @Post(path = "/some/{id}/{username}")
  public void somethingPost (Request request, Response response) {

    String username = request.getParam("username");
    String id = request.getParam("id");

    Gson body = new Gson();
//    Person person = body.fromJson(request.getBody(), Person.class);

//    System.out.println(person.getName());
//    System.out.println(person.getSurname());
//    System.out.println(person.getAge());

    Map<String, Object> resMap = new LinkedHashMap<>();
    resMap.put("id", id);
    resMap.put("username", username);

    Gson res = new Gson();

    response
        .setStatus(200)
//        .setBody(res.toJson(resMap))
        .setBody(request.getBody())
        .send();
  }

  @Get(path = "/realtime")
  public void realTime(Request request, Response response) {

    PrintWriter writer = response.getPrintWriter();
    writer.println("HTTP/2 200 OK");
    writer.println("Access-Control-Allow-Headers: x-prototype-version,x-requested-with");
    writer.println("Access-Control-Allow-Methods: GET,POST");
    writer.println("Access-Control-Allow-Origin: *");
    writer.println("Cache-Control: no-store");
    writer.println("Content-Type: text/event-stream; charset=UTF-8");
    writer.println("Connection: keep-alive");
    writer.println();
    writer.flush();

    while (true) {
      System.out.println("... sending ...");
      writer.print("id: thisWouldBeAnId\n");
      writer.print("event: messageEvent\n");
      writer.print("data: {\"some\": \"data\"}\n\n");
      writer.flush();
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

  }

  @Get(path = "/test")
  public void testing(Request request, Response response) {

    response.send();
  }
}

class Person {
  private String name;
  private String surname;
  private int age;
  Person() {}

  public String getName() {
    return this.name;
  }

  public String getSurname() {
    return this.surname;
  }

  public int getAge() {
    return this.age;
  }
}