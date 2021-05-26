package uk.gov.ons.census.bulkprocessor.validation;

import java.util.Optional;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.ons.census.bulkprocessor.client.CaseApiClient;
import uk.gov.ons.census.bulkprocessor.config.ApplicationContextProvider;

public class CaseIdExistsRule implements Rule {
  private CaseApiClient caseApiClient = null;

  @Override
  public Optional<String> checkValidity(String data) {
    try {
      getCaseApiClient().getCase(data);

      return Optional.empty();
    } catch (HttpClientErrorException e) {
      if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
        return Optional.of("Case does not exist");
      }

      throw e;
    }
  }

  private CaseApiClient getCaseApiClient() {
    if (caseApiClient == null) {
      ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
      caseApiClient = applicationContext.getBean(CaseApiClient.class);
    }

    return caseApiClient;
  }
}
