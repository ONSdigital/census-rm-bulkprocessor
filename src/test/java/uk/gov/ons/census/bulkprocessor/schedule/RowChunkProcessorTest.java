package uk.gov.ons.census.bulkprocessor.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import uk.gov.ons.census.bulkprocessor.model.dto.CreateCaseSample;
import uk.gov.ons.census.bulkprocessor.model.entity.BulkProcess;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRow;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRowStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRowRepository;

@RunWith(MockitoJUnitRunner.class)
public class RowChunkProcessorTest {
  @Mock private JobRowRepository jobRowRepository;

  @Mock private RabbitTemplate rabbitTemplate;

  @InjectMocks private RowChunkProcessor underTest;

  @Test
  public void testStageChunk() {
    Job job = new Job();
    job.setBulkProcess(BulkProcess.NEW_ADDRESS);

    Map<String, String> rowData = new HashMap<>();
    rowData.put("ADDRESS_LINE1", "test address line 1");
    rowData.put("UPRN", "test uprn");
    rowData.put("POSTCODE", "test postcode");
    rowData.put("ADDRESS_TYPE", "HH");
    rowData.put("CE_EXPECTED_CAPACITY", "666");
    rowData.put("CE_SECURE", "1");

    JobRow jobRow = new JobRow();
    jobRow.setRowData(rowData);

    when(jobRowRepository.findTop500ByJobAndAndJobRowStatus(job, JobRowStatus.STAGED))
        .thenReturn(List.of(jobRow));

    boolean result = underTest.processChunk(job);

    assertThat(result).isFalse();

    ArgumentCaptor<CreateCaseSample> sampleArgumentCaptor =
        ArgumentCaptor.forClass(CreateCaseSample.class);
    verify(rabbitTemplate)
        .convertAndSend(eq(""), eq("case.sample.inbound"), sampleArgumentCaptor.capture());
    assertThat(sampleArgumentCaptor.getValue().getAddressLine1()).isEqualTo("test address line 1");

    ArgumentCaptor<List<JobRow>> jobRowListArgumentCaptor = ArgumentCaptor.forClass(List.class);
    verify(jobRowRepository).saveAll(jobRowListArgumentCaptor.capture());
    assertThat(jobRowListArgumentCaptor.getValue().size()).isEqualTo(1);
    assertThat(jobRowListArgumentCaptor.getValue().get(0)).isEqualTo(jobRow);
    assertThat(jobRowListArgumentCaptor.getValue().get(0).getJobRowStatus())
        .isEqualTo(JobRowStatus.PROCESSED_OK);
  }

  @Test
  public void testStageChunkFailedValidation() {
    Job job = new Job();
    job.setBulkProcess(BulkProcess.NEW_ADDRESS);

    Map<String, String> rowData = new HashMap<>();
    rowData.put("ADDRESS_LINE1", "test address line 1");
    rowData.put("UPRN", "test uprn");
    rowData.put("POSTCODE", "%%$*&$£&^$£*");
    rowData.put("ADDRESS_TYPE", "HH");
    rowData.put("CE_EXPECTED_CAPACITY", "666");
    rowData.put("CE_SECURE", "1");

    JobRow jobRow = new JobRow();
    jobRow.setRowData(rowData);

    when(jobRowRepository.findTop500ByJobAndAndJobRowStatus(job, JobRowStatus.STAGED))
        .thenReturn(List.of(jobRow));

    boolean result = underTest.processChunk(job);

    assertThat(result).isTrue();

    verifyNoInteractions(rabbitTemplate);

    ArgumentCaptor<List<JobRow>> jobRowListArgumentCaptor = ArgumentCaptor.forClass(List.class);
    verify(jobRowRepository).saveAll(jobRowListArgumentCaptor.capture());
    assertThat(jobRowListArgumentCaptor.getValue().size()).isEqualTo(1);
    assertThat(jobRowListArgumentCaptor.getValue().get(0)).isEqualTo(jobRow);
    assertThat(jobRowListArgumentCaptor.getValue().get(0).getJobRowStatus())
        .isEqualTo(JobRowStatus.PROCESSED_ERROR);
    assertThat(jobRowListArgumentCaptor.getValue().get(0).getValidationErrorDescriptions())
        .isEqualTo(
            "Column 'POSTCODE' value '%%$*&$£&^$£*' validation error: Value is not alphanumeric");
  }
}
