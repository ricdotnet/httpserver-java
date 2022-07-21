package Test;

import dev.ricr.Configurations.EchoerConfigurations;
import dev.ricr.Echoer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class GetRequestTest {

  static Echoer echoer = null;

  @BeforeClass
  public static void createServer () {
    EchoerConfigurations.setAppPort(4000);
    EchoerConfigurations.setAppName("Echoer Tests");
    EchoerConfigurations.setControllersPackage("Test.Controllers");
    EchoerConfigurations.setServicesPackage("Test.Services");

    System.out.println("hi");
    Thread t1 = new Thread(() -> echoer = new Echoer(), "t1");
    t1.start();
  }

  @Test
  public void echoerToBeTruthy () {
    System.out.println(Thread.currentThread().getName());
//    assertNotNull(echoer);
//    echoer.stop();
  }

  @AfterClass
  public static void after() {
    System.out.println(echoer != null);
  }

}
