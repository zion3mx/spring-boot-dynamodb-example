package nz.mikhailov.example.healthcheck;

import nz.mikhailov.example.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest({"server.port=0"})
public class HealthCheckIntegrationTest {

  @Value("${local.server.port}")
  private int port;

  @Test
  public void healthCheckShouldReturnUp() throws Exception {

    RestTemplate restTemplate = new TestRestTemplate();
    ResponseEntity<String> response = restTemplate.getForEntity(url("/health"), String.class);
    assertThat(response.getStatusCode(), is(OK));
    assertThat(response.getBody(), is("up"));
  }

  private URI url(String url) {

    return URI.create("http://localhost:" + port + url);
  }
}
