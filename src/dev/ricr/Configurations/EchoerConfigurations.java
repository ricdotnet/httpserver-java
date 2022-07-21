package dev.ricr.Configurations;

public class EchoerConfigurations {

  public static int APP_PORT = 4000;
  public static String APP_NAME = "SOME APP";

  // This is used for reflection and helps the app find your controllers and services.
  public static String CONTROLLERS_PACKAGE = "dev.ricr";
  public static String SERVICES_PACKAGE = "dev.ricr";

  public static Class<?>[] globalMiddlewares;

  public static void setAppPort (int appPort) {
    APP_PORT = appPort;
  }

  public static void setAppName (String appName) {
    APP_NAME = appName;
  }

  public static void setControllersPackage (String controllersPackage) {
    CONTROLLERS_PACKAGE = controllersPackage;
  }

  public static void setServicesPackage (String servicesPackage) {
    SERVICES_PACKAGE = servicesPackage;
  }
}
