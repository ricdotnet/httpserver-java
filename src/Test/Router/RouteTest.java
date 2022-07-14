package Test.Router;

import static org.junit.Assert.*;

import dev.ricr.Router.Router;
import org.junit.Test;

public class RouteTest {

  @Test
  public void ControllerHasPath() {
    var router = new Router();
    router.buildRoutes();

    assertFalse(router.getRoutesMap().isEmpty());
  }

}
