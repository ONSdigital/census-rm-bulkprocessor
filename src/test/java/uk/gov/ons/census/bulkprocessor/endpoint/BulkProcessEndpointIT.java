package uk.gov.ons.census.bulkprocessor.endpoint;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.census.bulkprocessor.model.dto.BulkProcessDto;

@ContextConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BulkProcessEndpointIT {

  @LocalServerPort private int port;

  String bulkProcessorUrl = "";

  @Before
  public void setUp() {
    bulkProcessorUrl = "http://localhost:" + port + "/bulkprocess";
  }

  @Test
  public void testBulkProcessorEndpoint() {
    RestTemplate restTemplate = new RestTemplate();

    ResponseEntity<BulkProcessDto[]> bulkprocessesDtosResponse =
        restTemplate.getForEntity(bulkProcessorUrl, BulkProcessDto[].class);
    assertThat(bulkprocessesDtosResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    BulkProcessDto[] bulkProcessesDto = bulkprocessesDtosResponse.getBody();

    assertThat(bulkProcessesDto.length).isEqualTo(2);
    assertThat(bulkProcessesDto[0].getBulkProcess()).isEqualTo("NEW_ADDRESS");
    assertThat(bulkProcessesDto[1].getBulkProcess()).isEqualTo("REFUSAL");
  }
}
