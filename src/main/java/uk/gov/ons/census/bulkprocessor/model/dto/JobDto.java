package uk.gov.ons.census.bulkprocessor.model.dto;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class JobDto {
  private UUID id;

  private String bulkProcess;

  private OffsetDateTime createdAt;
  private OffsetDateTime lastUpdatedAt;

  private String fileName;

  private JobStatusDto jobStatus;

  private int rowCount;
  private int rowErrorCount;

  private String fatalErrorDescription;
}
