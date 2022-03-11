package dev.ricr.Interfaces;

import dev.ricr.Router.Controller;
import java.util.List;

public interface IRouter {

  void setRoutes();
  List<Controller> getRoutes();

}
