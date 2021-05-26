package uk.gov.ons.census.bulkprocessor.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.UUID;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class TestCollectionCase {
  private UUID id;
  private TestAddress address;
  private RefusalTypeDTO refusalReceived;
}
