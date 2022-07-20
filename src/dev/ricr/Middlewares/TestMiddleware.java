package dev.ricr.Middlewares;

import dev.ricr.Interfaces.IMiddleware;

public class TestMiddleware implements IMiddleware {
  @Override
  public void run () {
    System.out.println("Running a middleware....");
  }
}
