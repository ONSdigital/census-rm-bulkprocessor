package uk.gov.ons.census.bulkprocessor.transformer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.Test;
import uk.gov.ons.census.bulkprocessor.model.dto.EventTypeDTO;
import uk.gov.ons.census.bulkprocessor.model.dto.RefusalTypeDTO;
import uk.gov.ons.census.bulkprocessor.model.dto.ResponseManagementEvent;

public class RefusalTransformerTest {
  @Test
  public void testTransformRow() {
    RefusalTransformer underTest = new RefusalTransformer();

    UUID testCaseId = UUID.randomUUID();
    Map<String, String> refusalRow = new HashMap<>();
    refusalRow.put("case_id", testCaseId.toString());
    refusalRow.put("refusal_type", RefusalTypeDTO.EXTRAORDINARY_REFUSAL.name());

    ResponseManagementEvent transformedRefusalRow =
        (ResponseManagementEvent) underTest.transformRow(refusalRow);
    assertThat(transformedRefusalRow.getEvent().getType()).isEqualTo(EventTypeDTO.REFUSAL_RECEIVED);
    assertThat(transformedRefusalRow.getPayload().getRefusal().getCollectionCase().getId())
        .isEqualTo(testCaseId);
    assertThat(transformedRefusalRow.getPayload().getRefusal().getType())
        .isEqualTo(RefusalTypeDTO.EXTRAORDINARY_REFUSAL);
  }
}
