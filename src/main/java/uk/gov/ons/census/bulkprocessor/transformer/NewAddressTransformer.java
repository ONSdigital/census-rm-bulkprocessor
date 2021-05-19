package uk.gov.ons.census.bulkprocessor.transformer;

import java.util.Map;
import java.util.UUID;
import uk.gov.ons.census.bulkprocessor.model.dto.CreateCaseSample;

public class NewAddressTransformer implements Transformer {
  private static final UUID CENSUS_ACTION_PLAN_ID =
      UUID.fromString("c4415287-0e37-447b-9c3d-1a011c9fa3db");
  private static final UUID CENSUS_COLLEX_ID =
      UUID.fromString("34d7f3bb-91c9-45d0-bb2d-90afce4fc790");

  @Override
  public Object transformRow(Map<String, String> rowData) {
    CreateCaseSample createCaseSample = new CreateCaseSample();
    createCaseSample.setAbpCode(rowData.get("ABP_CODE"));
    createCaseSample.setActionPlanId(CENSUS_ACTION_PLAN_ID);
    createCaseSample.setAddressLevel(rowData.get("ADDRESS_LEVEL"));
    createCaseSample.setAddressLine1(rowData.get("ADDRESS_LINE1"));
    createCaseSample.setAddressLine2(rowData.get("ADDRESS_LINE2"));
    createCaseSample.setAddressLine3(rowData.get("ADDRESS_LINE3"));
    createCaseSample.setAddressType(rowData.get("ADDRESS_TYPE"));
    createCaseSample.setBulkProcessed(true);
    createCaseSample.setCeExpectedCapacity(Integer.parseInt(rowData.get("CE_EXPECTED_CAPACITY")));
    createCaseSample.setCollectionExerciseId(CENSUS_COLLEX_ID);
    createCaseSample.setEstabType(rowData.get("ESTAB_TYPE"));
    createCaseSample.setEstabUprn(rowData.get("ESTAB_UPRN"));
    createCaseSample.setFieldCoordinatorId(rowData.get("FIELDCOORDINATOR_ID"));
    createCaseSample.setFieldOfficerId(rowData.get("FIELDOFFICER_ID"));
    createCaseSample.setHtcDigital(rowData.get("HTC_DIGITAL"));
    createCaseSample.setHtcWillingness(rowData.get("HTC_WILLINGNESS"));
    createCaseSample.setLad(rowData.get("LAD"));
    createCaseSample.setLatitude(rowData.get("LATITUDE"));
    createCaseSample.setLongitude(rowData.get("LONGITUDE"));
    createCaseSample.setLsoa(rowData.get("LSOA"));
    createCaseSample.setMsoa(rowData.get("MSOA"));
    createCaseSample.setOa(rowData.get("OA"));
    createCaseSample.setOrganisationName(rowData.get("ORGANISATION_NAME"));
    createCaseSample.setPostcode(rowData.get("POSTCODE"));
    createCaseSample.setPrintBatch(rowData.get("PRINT_BATCH"));
    createCaseSample.setRegion(rowData.get("REGION"));
    createCaseSample.setSecureEstablishment(Integer.parseInt(rowData.get("CE_SECURE")));
    createCaseSample.setTownName(rowData.get("TOWN_NAME"));
    createCaseSample.setTreatmentCode(rowData.get("TREATMENT_CODE"));
    createCaseSample.setUprn(rowData.get("UPRN"));
    return createCaseSample;
  }
}
