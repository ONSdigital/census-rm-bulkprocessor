package uk.gov.ons.census.bulkprocessor.model.dto;

import lombok.Data;

@Data
public class ResponseManagementEvent {
  private EventDTO event;
  private PayloadDTO payload;
}
