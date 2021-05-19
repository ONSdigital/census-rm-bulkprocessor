package uk.gov.ons.census.bulkprocessor.validation;

import java.util.Optional;

public interface Rule {
  Optional<String> checkValidity(String data);
}
