package uk.gov.ons.census.bulkprocessor.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TestRefusalDTO {
  private RefusalTypeDTO type;
  private TestCollectionCase collectionCase;
  private TestAddress address;
}
