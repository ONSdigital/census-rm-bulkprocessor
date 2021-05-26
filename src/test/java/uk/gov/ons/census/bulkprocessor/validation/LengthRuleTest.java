package uk.gov.ons.census.bulkprocessor.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.Test;

public class LengthRuleTest {
  @Test
  public void testCheckValidity() {
    LengthRule underTest = new LengthRule(5);
    Optional<String> result = underTest.checkValidity("12345");
    assertThat(result.isPresent()).isFalse();
  }

  @Test
  public void testCheckValidityTooLongIsInvalid() {
    LengthRule underTest = new LengthRule(5);
    Optional<String> result = underTest.checkValidity("123456");
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isEqualTo("Exceeded max length of 5");
  }
}
