package nz.mikhailov.example.healthcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

  private final Logger log = LoggerFactory.getLogger(getClass());

  @RequestMapping("/health")
  public String healthCheck() {

    log.trace("Entering healthCheck()");
    return "up";
  }

}
