package Test;

import dev.ricr.Configurations.EchoerConfigurations;
import dev.ricr.Echoer;

public class GetRequestTest {

  public static void main(String[] args) {
    EchoerConfigurations.setAppPort(4001);
    EchoerConfigurations.setAppName("Echoer Tests");
    EchoerConfigurations.setControllersPackage("Test.Controllers");
    EchoerConfigurations.setServicesPackage("Test.Services");

    new Echoer().start();
  }

}
