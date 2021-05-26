package uk.gov.ons.census.bulkprocessor.transformer;

import java.util.Map;

public interface Transformer {
  Object transformRow(Map<String, String> rowData);
}
