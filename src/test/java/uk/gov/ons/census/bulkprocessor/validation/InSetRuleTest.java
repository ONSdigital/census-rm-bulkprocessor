package uk.gov.ons.census.bulkprocessor.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.Test;

public class InSetRuleTest {
  @Test
  public void testCheckValidity() {
    InSetRule underTest = new InSetRule(new String[] {"a", "b", "c"});
    Optional<String> result = underTest.checkValidity("b");
    assertThat(result.isPresent()).isFalse();
  }

  @Test
  public void testCheckValidityNotInSetInvalid() {
    InSetRule underTest = new InSetRule(new String[] {"a", "b", "c"});
    Optional<String> result = underTest.checkValidity("z");
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).startsWith("Not in set of");
  }
}
