package uk.gov.ons.census.bulkprocessor.transformer;

import java.util.Map;
import java.util.UUID;
import uk.gov.ons.census.bulkprocessor.model.dto.Sample;

public class NewAddressTransformer implements Transformer {
  protected static final UUID LMS_COLLEX_ID =
      UUID.fromString("0184cb41-0529-40ff-a2b7-08770249b95c");

  @Override
  public Object transformRow(Map<String, String> rowData) {
    Sample sample = new Sample();
    sample.setCaseId(UUID.randomUUID());
    sample.setCollectionExerciseId(LMS_COLLEX_ID);
    sample.setSample(rowData);
    return sample;
  }
}
