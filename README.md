### A simple java http server

This is just a means of learning and understanding more about http protocol and also java itself.<br>
When somewhat stable I will use on some of my APIs.

### TODO

- [x] Request and response objects

<!-- - [ ] Reflection (although it kinda works needs a lot of work still) -->

- [x] Multithreaded requests
- [ ] Response headers
- [x] Body serializer (json <-> java object) This was achieved with Gson, but I am planning to add some wrappers around
  it to make it easier for anyone to (de)serialize objects
- [x] x-www-form-urlencoded
- [ ] multipart/form-data
- [x] Better route system (kinda works OK)
- [x] Route parameters
- [x] Route query parameters
- [ ] Middlewares
- [ ] WebSockets and EventStreams

### Good for a try

#### Just clone and build and follow the following steps.

```java
import dev.ricr.Echoer;
import dev.ricr.Configurations.EchoerConfigurations;

public class App {
  public static void main (String[] args) {
    EchoerConfigurations.setAppPort(4001);
    EchoerConfigurations.setControllersPackage("your controllers package");
    // EchoerConfigurations.setControllersPackage("com.example.Controllers");
    EchoerConfigurations.setServicesPackage("your services package");
    //EchoerConfigurations.setServicesPackage("com.example.Services");

    new Echoer();
  }
}
```

<hr><br>
A controller would look like the following

```java
import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Context.Request;
import dev.ricr.Context.Response;

@Controller(path = "/controller")
public class TestController {

  @Get(path = "/route")
  public void hello (Request request, Response response) {
    response
        .setStatus(200)
        .setBody("some body")
        .send();
  }
}
```

<hr><br>
There is also a container to enable dependency injection / singletons, and it allows us to use "services"

A service would then look like the following

```java
import dev.ricr.Annotations.Service;

@Service
public class TestService {

  public String serviceMethod () {
    return "returned from the service method.";
  }
}
```

<br>
And would be used in a controller as follows

```java
import dev.ricr.Annotations.Controller;
import dev.ricr.Annotations.Get;
import dev.ricr.Context.Request;
import dev.ricr.Context.Response;

@Controller(path = "/controller")
public class TestController {

  TestService testService;

  public TestController (TestService testService) {
    this.testService = testService;
  }

  @Get(path = "/route")
  public void hello (Request request, Response response) {
    // and we would then be able to use its methods because
    //  it gets instantiated and injected at when booting the app
    testService.method();

    response
        .setStatus(200)
        .setBody("some body")
        .send();
  }
}
```

<hr><br>
We can also use custom route parameters and of course query parameters too

And to use them we just do the following

```java
import dev.ricr.Annotations.Controller;

@Controller("/controller")
public class Test {

  @Post(path = "/route/{username}")
  public void customParams (Request request, Response response) {
    // to access a route param
    String username = request.getParam("username");

    // to access a query param if any
    // (/route/{username}?email=email@example.com)
    String email = request.getQuery("email");
  }
}
```

<hr><br>
We have the ability to serve static files too. If we save some js/html/images in a folder "/assets"
we can then create a route that will serve those files.

```java
import dev.ricr.Echoer;
import dev.ricr.Configurations.EchoerConfigurations;
import dev.ricr.Router.Router;

public class App {
  public static void main (String[] args) {
    // ... all configuration ...

    new Echoer();

    // after creating an instance of Echoer
    // "/assets" would be the url route path
    // "./files" would be the folder those static files are in, assuming "./" as the project root
    Router.addStatic("/assets", "./files");
  }
}
```

### Some observation

- Services inside services does not work, they do not get injected and will throw errors at runtime.
- This is a WIP so a lot is still do be done.
- The jar is built with GSon just head over to their docs if you want to include in your requests.
- We can set custom headers and get request headers, we just need to use `request.getHeader("key")`
  and `request.setHeader("key", "value")`
- Access the request body with `request.getBody()`
- The request / response objects also include the client/socket object as well as the input / output streams, so we can
  directly write or read data
    - The above can be used for websockets (working on implementing it) or event streams (tested and included an
      example) for realtime connection