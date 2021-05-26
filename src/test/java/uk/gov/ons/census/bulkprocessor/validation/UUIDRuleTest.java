package uk.gov.ons.census.bulkprocessor.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
import org.junit.Test;

public class UUIDRuleTest {
  @Test
  public void testCheckValidity() {
    UUIDRule underTest = new UUIDRule();
    Optional<String> result = underTest.checkValidity(UUID.randomUUID().toString());
    assertThat(result.isPresent()).isFalse();
  }

  @Test
  public void testCheckValidityInvalidUuidIsInvalid() {
    UUIDRule underTest = new UUIDRule();
    Optional<String> result = underTest.checkValidity("this is not a valid UUID");
    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isEqualTo("Not a valid UUID");
  }
}
