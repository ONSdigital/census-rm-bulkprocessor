package uk.gov.ons.census.bulkprocessor.models.test_dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.census.bulkprocessor.model.dto.RefusalTypeDTO;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RefusalDTO {
  private RefusalTypeDTO type;
  private CollectionCase collectionCase;
  private Address address;
}
