package uk.gov.ons.census.bulkprocessor.transformer;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ons.census.bulkprocessor.transformer.NewAddressTransformer.CENSUS_ACTION_PLAN_ID;
import static uk.gov.ons.census.bulkprocessor.transformer.NewAddressTransformer.CENSUS_COLLEX_ID;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import uk.gov.ons.census.bulkprocessor.model.dto.CreateCaseSample;

public class NewAddressTransformerTest {
  @Test
  public void testTransformRow() {
    NewAddressTransformer underTest = new NewAddressTransformer();

    Map<String, String> newAddressRow = new HashMap<>();
    newAddressRow.put("ABP_CODE", "TEST_ABP_CODE");
    newAddressRow.put("ADDRESS_LEVEL", "TEST_ADDRESS_LEVEL");
    newAddressRow.put("ADDRESS_LINE1", "TEST_ADDRESS_LINE1");
    newAddressRow.put("ADDRESS_LINE2", "TEST_ADDRESS_LINE2");
    newAddressRow.put("ADDRESS_LINE3", "TEST_ADDRESS_LINE3");
    newAddressRow.put("ADDRESS_TYPE", "TEST_ADDRESS_TYPE");
    newAddressRow.put("CE_EXPECTED_CAPACITY", "666");
    newAddressRow.put("ESTAB_TYPE", "TEST_ESTAB_TYPE");
    newAddressRow.put("ESTAB_UPRN", "TEST_ESTAB_UPRN");
    newAddressRow.put("FIELDCOORDINATOR_ID", "TEST_FIELDCOORDINATOR_ID");
    newAddressRow.put("FIELDOFFICER_ID", "TEST_FIELDOFFICER_ID");
    newAddressRow.put("HTC_DIGITAL", "TEST_HTC_DIGITAL");
    newAddressRow.put("HTC_WILLINGNESS", "TEST_HTC_WILLINGNESS");
    newAddressRow.put("LAD", "TEST_LAD");
    newAddressRow.put("LATITUDE", "TEST_LATITUDE");
    newAddressRow.put("LONGITUDE", "TEST_LONGITUDE");
    newAddressRow.put("LSOA", "TEST_LSOA");
    newAddressRow.put("MSOA", "TEST_MSOA");
    newAddressRow.put("OA", "TEST_OA");
    newAddressRow.put("ORGANISATION_NAME", "TEST_ORGANISATION_NAME");
    newAddressRow.put("POSTCODE", "TEST_POSTCODE");
    newAddressRow.put("PRINT_BATCH", "TEST_PRINT_BATCH");
    newAddressRow.put("REGION", "TEST_REGION");
    newAddressRow.put("CE_SECURE", "1");
    newAddressRow.put("TOWN_NAME", "TEST_TOWN_NAME");
    newAddressRow.put("TREATMENT_CODE", "TEST_TREATMENT_CODE");
    newAddressRow.put("UPRN", "TEST_UPRN");

    CreateCaseSample transformedRow = (CreateCaseSample) underTest.transformRow(newAddressRow);

    assertThat(transformedRow.getAbpCode()).isEqualTo("TEST_ABP_CODE");
    assertThat(transformedRow.getActionPlanId()).isEqualTo(CENSUS_ACTION_PLAN_ID);
    assertThat(transformedRow.getAddressLevel()).isEqualTo("TEST_ADDRESS_LEVEL");
    assertThat(transformedRow.getAddressLine1()).isEqualTo("TEST_ADDRESS_LINE1");
    assertThat(transformedRow.getAddressLine2()).isEqualTo("TEST_ADDRESS_LINE2");
    assertThat(transformedRow.getAddressLine3()).isEqualTo("TEST_ADDRESS_LINE3");
    assertThat(transformedRow.getAddressType()).isEqualTo("TEST_ADDRESS_TYPE");
    assertThat(transformedRow.getCeExpectedCapacity()).isEqualTo(666);
    assertThat(transformedRow.getCollectionExerciseId()).isEqualTo(CENSUS_COLLEX_ID);
    assertThat(transformedRow.getEstabType()).isEqualTo("TEST_ESTAB_TYPE");
    assertThat(transformedRow.getEstabUprn()).isEqualTo("TEST_ESTAB_UPRN");
    assertThat(transformedRow.getFieldCoordinatorId()).isEqualTo("TEST_FIELDCOORDINATOR_ID");
    assertThat(transformedRow.getFieldOfficerId()).isEqualTo("TEST_FIELDOFFICER_ID");
    assertThat(transformedRow.getHtcDigital()).isEqualTo("TEST_HTC_DIGITAL");
    assertThat(transformedRow.getHtcWillingness()).isEqualTo("TEST_HTC_WILLINGNESS");
    assertThat(transformedRow.getLad()).isEqualTo("TEST_LAD");
    assertThat(transformedRow.getLatitude()).isEqualTo("TEST_LATITUDE");
    assertThat(transformedRow.getLongitude()).isEqualTo("TEST_LONGITUDE");
    assertThat(transformedRow.getLsoa()).isEqualTo("TEST_LSOA");
    assertThat(transformedRow.getMsoa()).isEqualTo("TEST_MSOA");
    assertThat(transformedRow.getOa()).isEqualTo("TEST_OA");
    assertThat(transformedRow.getOrganisationName()).isEqualTo("TEST_ORGANISATION_NAME");
    assertThat(transformedRow.getPostcode()).isEqualTo("TEST_POSTCODE");
    assertThat(transformedRow.getPrintBatch()).isEqualTo("TEST_PRINT_BATCH");
    assertThat(transformedRow.getRegion()).isEqualTo("TEST_REGION");
    assertThat(transformedRow.getSecureEstablishment()).isEqualTo(1);
    assertThat(transformedRow.getTownName()).isEqualTo("TEST_TOWN_NAME");
    assertThat(transformedRow.getTreatmentCode()).isEqualTo("TEST_TREATMENT_CODE");
    assertThat(transformedRow.getUprn()).isEqualTo("TEST_UPRN");
    assertThat(transformedRow.isBulkProcessed()).isEqualTo(true);
  }
}
