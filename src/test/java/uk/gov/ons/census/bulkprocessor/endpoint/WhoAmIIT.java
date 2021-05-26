package uk.gov.ons.census.bulkprocessor.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@ContextConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WhoAmIIT {

  @LocalServerPort private int port;

  @Test
  public void testWhoAmI() {
    String testUrl = "http://localhost:" + port + "/whoami";
    RestTemplate restTemplate = new RestTemplate();

    ResponseEntity<HashMap> actualJobsResponse = restTemplate.getForEntity(testUrl, HashMap.class);
    Map<String, String> whoAmIData = actualJobsResponse.getBody();
    assertThat(whoAmIData.get("user")).isEqualTo("dummy@fake-email.com");
  }
}
