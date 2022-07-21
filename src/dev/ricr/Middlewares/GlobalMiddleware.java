package dev.ricr.Middlewares;

import dev.ricr.Context.Request;
import dev.ricr.Interfaces.IMiddleware;

public class GlobalMiddleware implements IMiddleware {
  @Override
  public void run (Request request) {
    System.out.println("global middleware...");
    request.setData("username", request.getParam("username"));
    request.setData("name", request.getQuery("name"));
  }
}
