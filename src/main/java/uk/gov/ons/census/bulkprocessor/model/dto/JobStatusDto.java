package uk.gov.ons.census.bulkprocessor.model.dto;

public enum JobStatusDto {
  FILE_UPLOADED,
  FILE_STAGED,
  STAGING_IN_PROGRESS,
  PROCESSED_OK,
  PROCESSED_WITH_ERRORS,
  PROCESSED_TOTAL_FAILURE
}
