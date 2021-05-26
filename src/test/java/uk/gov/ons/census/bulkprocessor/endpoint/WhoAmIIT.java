package uk.gov.ons.census.bulkprocessor.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.census.bulkprocessor.model.dto.JobDto;

import javax.persistence.metamodel.Type;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WhoAmIIT {

    @LocalServerPort
    private int port;


    @Test
    public void testWhoAmI() {
        String testUrl = "http://localhost:" + port + "/whoami";
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<HashMap> actualJobsResponse = restTemplate.getForEntity(testUrl,  HashMap.class);
        Map<String, String> whoAmIData = actualJobsResponse.getBody();
        assertThat(whoAmIData.get("user")).isEqualTo("dummy@fake-email.com");
    }
}
