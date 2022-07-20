package Test.Services;

import dev.ricr.Annotations.Service;

@Service
public class TestService {

  public void serviceMethod(String country) {
    System.out.println("printing country from the service: " + country);
  }

}
