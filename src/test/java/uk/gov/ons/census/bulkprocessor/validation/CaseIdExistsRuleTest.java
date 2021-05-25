package uk.gov.ons.census.bulkprocessor.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import uk.gov.ons.census.bulkprocessor.client.CaseApiClient;
import uk.gov.ons.census.bulkprocessor.config.ApplicationContextProvider;

public class CaseIdExistsRuleTest {
  @Test
  public void testCheckValidity() {
    CaseIdExistsRule underTest = new CaseIdExistsRule();

    CaseApiClient caseApiClientMock = Mockito.mock(CaseApiClient.class);

    ApplicationContext appCtxMock = Mockito.mock(ApplicationContext.class);

    try (MockedStatic<ApplicationContextProvider> appCtxProviderMock =
        Mockito.mockStatic(ApplicationContextProvider.class)) {

      appCtxProviderMock
          .when(() -> ApplicationContextProvider.getApplicationContext())
          .thenReturn(appCtxMock);

      when(appCtxMock.getBean(CaseApiClient.class)).thenReturn(caseApiClientMock);

      UUID testCaseId = UUID.randomUUID();
      Optional<String> result = underTest.checkValidity(testCaseId.toString());
      assertThat(result.isPresent()).isFalse();

      verify(caseApiClientMock).getCase(testCaseId.toString());
    }
  }

  @Test
  public void testCheckValidityCaseNotFound() {
    CaseIdExistsRule underTest = new CaseIdExistsRule();

    CaseApiClient caseApiClientMock = Mockito.mock(CaseApiClient.class);

    ApplicationContext appCtxMock = Mockito.mock(ApplicationContext.class);

    try (MockedStatic<ApplicationContextProvider> appCtxProviderMock =
        Mockito.mockStatic(ApplicationContextProvider.class)) {

      appCtxProviderMock
          .when(() -> ApplicationContextProvider.getApplicationContext())
          .thenReturn(appCtxMock);

      when(appCtxMock.getBean(CaseApiClient.class)).thenReturn(caseApiClientMock);

      when(caseApiClientMock.getCase(anyString()))
          .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

      UUID testCaseId = UUID.randomUUID();
      Optional<String> result = underTest.checkValidity(testCaseId.toString());
      assertThat(result.isPresent()).isTrue();
      assertThat(result.get()).isEqualTo("Case does not exist");

      verify(caseApiClientMock).getCase(testCaseId.toString());
    }
  }
}
