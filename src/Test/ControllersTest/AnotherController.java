package Test.ControllersTest;

import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Annotations.Post;
import dev.ricr.Context.Request;
import dev.ricr.Context.Response;

import java.io.*;

@Controller(path = "/another")
public class AnotherController {

  @Post(path = "/test")
  public void anotherTest (Request request, Response response) {

//    System.out.println(request.getQuery("name"));
//    System.out.println(request.getHeaders());
//    System.out.println(request.getBody("name"));
//    System.out.println(request.getBody("surname"));
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

  @Get(path = "/image")
  public void image (Request request, Response response) {

    BufferedInputStream img;
    try {
      img = new BufferedInputStream(new FileInputStream("./files/image.jpeg"));
//    response.setHeader("content-type", "image/jpeg");
      response.getWriter()
          .write("HTTP/1.1 200 OK\r\n" +
              "Content-Type: image/jpeg\r\n" +
              "Content-Length: " + img.available() + "\r\n" +
              "\r\n");
      response.getWriter().flush();

      BufferedOutputStream bufOut = new BufferedOutputStream(request.getClient().getOutputStream());

      while (img.available() > 0) {
        bufOut.write(img.read());
      }
      bufOut.flush();
    } catch (IOException e) {
      response.setStatus(404).setBody("image not found").send();
      return;
    }

//    response.send();

  }

}

class Test {
  String name;
  String surname;

  public Test () {
  }
}