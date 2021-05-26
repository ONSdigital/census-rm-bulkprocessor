package uk.gov.ons.census.bulkprocessor.transformer;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import uk.gov.ons.census.bulkprocessor.model.dto.CollectionCase;
import uk.gov.ons.census.bulkprocessor.model.dto.EventDTO;
import uk.gov.ons.census.bulkprocessor.model.dto.EventTypeDTO;
import uk.gov.ons.census.bulkprocessor.model.dto.PayloadDTO;
import uk.gov.ons.census.bulkprocessor.model.dto.RefusalDTO;
import uk.gov.ons.census.bulkprocessor.model.dto.RefusalTypeDTO;
import uk.gov.ons.census.bulkprocessor.model.dto.ResponseManagementEvent;

public class RefusalTransformer implements Transformer {
  @Override
  public Object transformRow(Map<String, String> rowData) {
    ResponseManagementEvent rme = new ResponseManagementEvent();

    EventDTO event = new EventDTO();
    event.setType(EventTypeDTO.REFUSAL_RECEIVED);
    event.setSource("RM_BULK_REFUSAL_PROCESSOR");
    event.setChannel("RM");
    event.setDateTime(OffsetDateTime.now());
    event.setTransactionId(UUID.randomUUID());
    rme.setEvent(event);

    PayloadDTO payload = new PayloadDTO();

    RefusalDTO refusal = new RefusalDTO();
    refusal.setType(RefusalTypeDTO.valueOf(rowData.get("refusal_type")));

    CollectionCase collectionCase = new CollectionCase();
    collectionCase.setId(UUID.fromString(rowData.get("case_id")));
    refusal.setCollectionCase(collectionCase);

    payload.setRefusal(refusal);
    rme.setPayload(payload);

    return rme;
  }
}
