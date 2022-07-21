package dev.ricr.Interfaces;

import dev.ricr.Context.Request;
import dev.ricr.Context.Response;

public interface IMiddleware {

  public void run (Request request);

}
