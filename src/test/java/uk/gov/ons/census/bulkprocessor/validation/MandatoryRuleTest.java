package uk.gov.ons.census.bulkprocessor.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.Test;

public class MandatoryRuleTest {
  @Test
  public void testCheckValidity() {
    MandatoryRule underTest = new MandatoryRule();
    Optional<String> result = underTest.checkValidity("here is a value");
    assertThat(result.isPresent()).isFalse();
  }

  @Test
  public void testCheckValidityNoValueIsInvalid() {
    MandatoryRule underTest = new MandatoryRule();
    Optional<String> result = underTest.checkValidity("");
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isEqualTo("Mandatory value missing");
  }

  @Test
  public void testCheckValidityOnlyWhitespaceIsInvalid() {
    MandatoryRule underTest = new MandatoryRule();
    Optional<String> result = underTest.checkValidity("   ");
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isEqualTo("Mandatory value missing");
  }
}
