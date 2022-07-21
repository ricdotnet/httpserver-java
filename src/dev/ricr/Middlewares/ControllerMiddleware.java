package dev.ricr.Middlewares;

import dev.ricr.Context.Request;
import dev.ricr.Interfaces.IMiddleware;

public class ControllerMiddleware implements IMiddleware {
  @Override
  public void run (Request request) {
    System.out.println("this is a controller middleware...");
  }
}
