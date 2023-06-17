package dev.ricr.Interfaces;

import dev.ricr.Router.Route;

import java.util.ArrayList;
import java.util.HashMap;

public interface IRouter {

  private void buildRoutes() {}

  HashMap<String, ArrayList<Route<Object>>> getRoutesMap();

}
