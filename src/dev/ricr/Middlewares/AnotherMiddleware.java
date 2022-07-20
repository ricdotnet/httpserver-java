package dev.ricr.Middlewares;

import dev.ricr.Interfaces.IMiddleware;

public class AnotherMiddleware implements IMiddleware {
  @Override
  public void run () {
    System.out.println("Running another middleware.");
  }
}
