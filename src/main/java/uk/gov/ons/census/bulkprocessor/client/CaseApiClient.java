package uk.gov.ons.census.bulkprocessor.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.ons.census.bulkprocessor.model.dto.CaseDetailsDTO;

@Component
public class CaseApiClient {

  @Value("${caseapi.connection.scheme}")
  private String scheme;

  @Value("${caseapi.connection.host}")
  private String host;

  @Value("${caseapi.connection.port}")
  private String port;

  public CaseDetailsDTO getCase(String caseId) {
    RestTemplate restTemplate = new RestTemplate();

    UriComponents uriComponents = createUriComponents(caseId);
    ResponseEntity<CaseDetailsDTO> responseEntity =
        restTemplate.exchange(uriComponents.toUri(), HttpMethod.GET, null, CaseDetailsDTO.class);

    return responseEntity.getBody();
  }

  private UriComponents createUriComponents(String caseId) {
    return UriComponentsBuilder.newInstance()
        .scheme(scheme)
        .host(host)
        .port(port)
        .pathSegment("cases", caseId)
        .build()
        .encode();
  }
}
