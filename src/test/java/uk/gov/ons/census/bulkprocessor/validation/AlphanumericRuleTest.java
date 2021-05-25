package uk.gov.ons.census.bulkprocessor.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.Test;

public class AlphanumericRuleTest {
  @Test
  public void testCheckValidityAlphanumericValueIsValid() {
    AlphanumericRule underTest = new AlphanumericRule();

    Optional<String> result = underTest.checkValidity("abc123XYZ99");
    assertThat(result.isPresent()).isFalse();
  }

  @Test
  public void testCheckValidityNonAlphanumericValueIsInvalid() {
    AlphanumericRule underTest = new AlphanumericRule();

    Optional<String> result = underTest.checkValidity("--%%££");
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isEqualTo("Value is not alphanumeric");
  }
}
