package uk.gov.ons.census.bulkprocessor.model.entity;

public enum JobStatus {
  FILE_UPLOADED,
  FILE_STAGED,
  STAGING_IN_PROGRESS,
  PROCESSED_OK,
  PROCESSED_WITH_ERRORS,
  PROCESSED_TOTAL_FAILURE
}
