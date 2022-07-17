package dev.ricr.ControllersTest;

import com.google.gson.Gson;
import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Post;
import dev.ricr.Context.Request;
import dev.ricr.Context.Response;

@Controller(path = "/another")
public class AnotherController {

  @Post(path = "/test")
  public void anotherTest (Request request, Response response) {

//    System.out.println(request.getQuery("name"));
//    System.out.println(request.getHeaders());
    System.out.println(request.getBody("name"));
    System.out.println(request.getBody("surname"));
//    System.out.println(request.getBody("address1"));
//    System.out.println(request.getBody("address2"));
//    System.out.println(request.getBody("postcode"));
//    System.out.println(request.getBody("county"));
//    System.out.println(request.getBody("country"));
//    System.out.println(request.getBody());

//    System.out.println(request.getHeader("content-length"));

//    Gson body = new Gson();
//    Test test = body.fromJson(request.getBody(), Test.class);
//
//    System.out.println(test.name);
//    System.out.println(test.surname);

    response.setStatus(200).send();
  }

}

class Test {
  String name;
  String surname;

  public Test () {
  }
}