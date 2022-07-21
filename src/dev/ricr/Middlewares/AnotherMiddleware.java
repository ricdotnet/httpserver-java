package dev.ricr.Middlewares;

import dev.ricr.Context.Request;
import dev.ricr.Interfaces.IMiddleware;

public class AnotherMiddleware implements IMiddleware {
  @Override
  public void run (Request request) {
    System.out.println("Running another route middleware.");
  }
}
