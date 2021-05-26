package uk.gov.ons.census.bulkprocessor.model.entity;

import uk.gov.ons.census.bulkprocessor.transformer.NewAddressTransformer;
import uk.gov.ons.census.bulkprocessor.transformer.RefusalTransformer;
import uk.gov.ons.census.bulkprocessor.transformer.Transformer;
import uk.gov.ons.census.bulkprocessor.validation.AlphanumericRule;
import uk.gov.ons.census.bulkprocessor.validation.CaseIdExistsRule;
import uk.gov.ons.census.bulkprocessor.validation.ColumnValidator;
import uk.gov.ons.census.bulkprocessor.validation.InSetRule;
import uk.gov.ons.census.bulkprocessor.validation.LengthRule;
import uk.gov.ons.census.bulkprocessor.validation.MandatoryRule;
import uk.gov.ons.census.bulkprocessor.validation.Rule;
import uk.gov.ons.census.bulkprocessor.validation.UUIDRule;

public enum BulkProcess {
  NEW_ADDRESS(
      "New Address",
      new String[] {
        "UPRN",
        "ESTAB_UPRN",
        "ADDRESS_TYPE",
        "ESTAB_TYPE",
        "ADDRESS_LEVEL",
        "ABP_CODE",
        "ORGANISATION_NAME",
        "ADDRESS_LINE1",
        "ADDRESS_LINE2",
        "ADDRESS_LINE3",
        "TOWN_NAME",
        "POSTCODE",
        "LATITUDE",
        "LONGITUDE",
        "OA",
        "LSOA",
        "MSOA",
        "LAD",
        "REGION",
        "HTC_WILLINGNESS",
        "HTC_DIGITAL",
        "TREATMENT_CODE",
        "FIELDCOORDINATOR_ID",
        "FIELDOFFICER_ID",
        "CE_EXPECTED_CAPACITY",
        "CE_SECURE",
        "PRINT_BATCH"
      },
      new ColumnValidator[] {
        new ColumnValidator("ADDRESS_LINE1", new Rule[] {new MandatoryRule(), new LengthRule(60)}),
        new ColumnValidator("UPRN", new Rule[] {new MandatoryRule()}),
        new ColumnValidator("POSTCODE", new Rule[] {new MandatoryRule(), new AlphanumericRule()}),
        new ColumnValidator(
            "ADDRESS_TYPE", new Rule[] {new InSetRule(new String[] {"HH", "CE", "SPG"})})
      },
      new NewAddressTransformer(),
      "",
      "case.sample.inbound"),
  REFUSAL(
      "Refusal",
      new String[] {"case_id", "refusal_type"},
      new ColumnValidator[] {
        new ColumnValidator(
            "case_id", new Rule[] {new MandatoryRule(), new UUIDRule(), new CaseIdExistsRule()}),
        new ColumnValidator(
            "refusal_type",
            new Rule[] {
              new MandatoryRule(),
              new InSetRule(new String[] {"HARD_REFUSAL", "EXTRAORDINARY_REFUSAL"})
            })
      },
      new RefusalTransformer(),
      "events",
      "event.respondent.refusal");

  BulkProcess(
      String title,
      String[] expectedColumns,
      ColumnValidator[] columnValidators,
      Transformer transformer,
      String targetExchange,
      String targetRoutingKey) {
    this.title = title;

    this.expectedColumns = expectedColumns;
    this.columnValidators = columnValidators;
    this.transformer = transformer;
    this.targetExchange = targetExchange;
    this.targetRoutingKey = targetRoutingKey;
  }

  private final String title;
  private final String[] expectedColumns;
  private final ColumnValidator[] columnValidators;
  private final Transformer transformer;
  private final String targetExchange;
  private final String targetRoutingKey;

  public String[] getExpectedColumns() {
    return expectedColumns;
  }

  public ColumnValidator[] getColumnValidators() {
    return columnValidators;
  }

  public Transformer getTransformer() {
    return transformer;
  }

  public String getTargetExchange() {
    return targetExchange;
  }

  public String getTargetRoutingKey() {
    return targetRoutingKey;
  }

  public String getTitle() {
    return title;
  }
}
