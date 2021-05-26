package uk.gov.ons.census.bulkprocessor.models.test_dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.UUID;
import lombok.Data;
import uk.gov.ons.census.bulkprocessor.model.dto.RefusalTypeDTO;

@Data
@JsonInclude(Include.NON_NULL)
public class CollectionCase {
  private UUID id;
  private Address address;
  private RefusalTypeDTO refusalReceived;
}
