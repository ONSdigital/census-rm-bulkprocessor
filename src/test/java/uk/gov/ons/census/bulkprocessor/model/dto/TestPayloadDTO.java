package uk.gov.ons.census.bulkprocessor.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class TestPayloadDTO {
  private TestCollectionCase collectionCase;
  private TestRefusalDTO refusal;
}
