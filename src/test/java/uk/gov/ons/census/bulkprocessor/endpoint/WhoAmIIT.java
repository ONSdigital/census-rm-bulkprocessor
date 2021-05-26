package uk.gov.ons.census.bulkprocessor.endpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@ContextConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WhoAmIIT {

    @LocalServerPort
    private int port;


//    @Test
//    public void testWhoAmI() {
//        String testUrl = "http://localhost:" + port + "/job";
//        RestTemplate restTemplate = new RestTemplate();
//        Map<String, String> a;
//
//        restTemplate.getForEntity(testUrl, Map<String, String>.class)
//
//    }
}
