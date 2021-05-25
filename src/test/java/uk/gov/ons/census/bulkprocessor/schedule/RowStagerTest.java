package uk.gov.ons.census.bulkprocessor.schedule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.opencsv.CSVReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRepository;

@RunWith(MockitoJUnitRunner.class)
public class RowStagerTest {
  @Mock private JobRepository jobRepository;

  @Mock private RowChunkStager rowChunkStager;

  @InjectMocks private RowStager underTest;

  @Test
  public void testProcessRows() {
    UUID randomFileName = UUID.randomUUID();
    try (FileOutputStream fos = new FileOutputStream("/tmp/" + randomFileName)) {
      fos.write("case_id,refusal_type\n".getBytes());
      fos.write("e932fea8-aa40-4052-b796-12cb2f2517d2,EXTRAORDINARY_REFUSAL\n".getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Job job = mock(Job.class);
    when(job.getFileId()).thenReturn(randomFileName);
    when(job.getStagingRowNumber()).thenReturn(0).thenReturn(0).thenReturn(1).thenReturn(2);
    when(job.getFileRowCount()).thenReturn(3);

    when(jobRepository.findByJobStatus(JobStatus.STAGING_IN_PROGRESS)).thenReturn(List.of(job));

    underTest.processRows();

    verify(rowChunkStager, times(2))
        .stageChunk(eq(job), eq(new String[] {"case_id", "refusal_type"}), any(CSVReader.class));

    verify(job).setJobStatus(JobStatus.PROCESSING_IN_PROGRESS);
    verify(jobRepository).saveAndFlush(job);
  }
}
