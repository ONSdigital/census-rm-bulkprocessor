package uk.gov.ons.census.bulkprocessor.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opencsv.CSVReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.census.bulkprocessor.model.entity.BulkProcess;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRow;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRowStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRepository;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRowRepository;

@RunWith(MockitoJUnitRunner.class)
public class RowChunkStagerTest {
  @Mock private JobRepository jobRepository;
  @Mock private JobRowRepository jobRowRepository;

  @InjectMocks private RowChunkStager underTest;

  @Test
  public void testStageChunk() throws IOException {
    UUID randomFileName = UUID.randomUUID();
    try (FileOutputStream fos = new FileOutputStream("/tmp/" + randomFileName)) {
      fos.write("case_id,refusal_type\n".getBytes());
      fos.write("e932fea8-aa40-4052-b796-12cb2f2517d2,EXTRAORDINARY_REFUSAL\n".getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Job job = new Job();
    job.setBulkProcess(BulkProcess.REFUSAL);
    job.setFileId(randomFileName);

    CSVReader csvReader = mock(CSVReader.class);
    when(csvReader.readNext())
        .thenReturn(new String[] {"e932fea8-aa40-4052-b796-12cb2f2517d2", "EXTRAORDINARY_REFUSAL"})
        .thenReturn(null);

    underTest.stageChunk(job, new String[] {"case_id", "refusal_type"}, csvReader);

    ArgumentCaptor<List<JobRow>> jobRowArgumentCaptor = ArgumentCaptor.forClass(List.class);
    verify(jobRowRepository).saveAll(jobRowArgumentCaptor.capture());
    assertThat(jobRowArgumentCaptor.getValue().size()).isEqualTo(1);
    assertThat(jobRowArgumentCaptor.getValue().get(0).getJob()).isEqualTo(job);
    assertThat(jobRowArgumentCaptor.getValue().get(0).getJobRowStatus())
        .isEqualTo(JobRowStatus.STAGED);
    assertThat(jobRowArgumentCaptor.getValue().get(0).getOriginalRowLineNumber()).isEqualTo(1);
    assertThat(jobRowArgumentCaptor.getValue().get(0).getRowData().get("case_id"))
        .isEqualTo("e932fea8-aa40-4052-b796-12cb2f2517d2");
    assertThat(jobRowArgumentCaptor.getValue().get(0).getRowData().get("refusal_type"))
        .isEqualTo("EXTRAORDINARY_REFUSAL");

    ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
    verify(jobRepository).saveAndFlush(jobArgumentCaptor.capture());
    assertThat(jobArgumentCaptor.getValue()).isEqualTo(job);
    assertThat(jobArgumentCaptor.getValue().getStagingRowNumber()).isEqualTo(1);
  }
}
