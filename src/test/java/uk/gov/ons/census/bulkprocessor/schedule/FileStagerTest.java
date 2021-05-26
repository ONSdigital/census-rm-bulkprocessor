package uk.gov.ons.census.bulkprocessor.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.ons.census.bulkprocessor.model.entity.JobStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRepository;

@RunWith(MockitoJUnitRunner.class)
public class FileStagerTest {
  @Mock private JobRepository jobRepository;

  @InjectMocks private FileStager underTest;

  @Test
  public void testProcessFiles() {
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

    when(jobRepository.findByJobStatus(JobStatus.FILE_UPLOADED)).thenReturn(List.of(job));

    underTest.processFiles();

    ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
    verify(jobRepository).saveAndFlush(jobArgumentCaptor.capture());

    assertThat(jobArgumentCaptor.getValue()).isEqualTo(job);
    assertThat(jobArgumentCaptor.getValue().getJobStatus())
        .isEqualTo(JobStatus.STAGING_IN_PROGRESS);
  }

  @Test
  public void testProcessFilesNotEnoughColumns() {
    UUID randomFileName = UUID.randomUUID();
    try (FileOutputStream fos = new FileOutputStream("/tmp/" + randomFileName)) {
      fos.write("case_id\n".getBytes());
      fos.write("e932fea8-aa40-4052-b796-12cb2f2517d2\n".getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Job job = new Job();
    job.setBulkProcess(BulkProcess.REFUSAL);
    job.setFileId(randomFileName);

    when(jobRepository.findByJobStatus(JobStatus.FILE_UPLOADED)).thenReturn(List.of(job));

    underTest.processFiles();

    ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
    verify(jobRepository).saveAndFlush(jobArgumentCaptor.capture());

    assertThat(jobArgumentCaptor.getValue()).isEqualTo(job);
    assertThat(jobArgumentCaptor.getValue().getJobStatus())
        .isEqualTo(JobStatus.PROCESSED_TOTAL_FAILURE);
    assertThat(jobArgumentCaptor.getValue().getFatalErrorDescription())
        .isEqualTo("Header row does not have expected number of columns");
  }

  @Test
  public void testProcessFilesIncorrectColumns() {
    UUID randomFileName = UUID.randomUUID();
    try (FileOutputStream fos = new FileOutputStream("/tmp/" + randomFileName)) {
      fos.write("foo,bar\n".getBytes());
      fos.write("e932fea8-aa40-4052-b796-12cb2f2517d2,EXTRAORDINARY_REFUSAL\n".getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Job job = new Job();
    job.setBulkProcess(BulkProcess.REFUSAL);
    job.setFileId(randomFileName);

    when(jobRepository.findByJobStatus(JobStatus.FILE_UPLOADED)).thenReturn(List.of(job));

    underTest.processFiles();

    ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
    verify(jobRepository).saveAndFlush(jobArgumentCaptor.capture());

    assertThat(jobArgumentCaptor.getValue()).isEqualTo(job);
    assertThat(jobArgumentCaptor.getValue().getJobStatus())
        .isEqualTo(JobStatus.PROCESSED_TOTAL_FAILURE);
    assertThat(jobArgumentCaptor.getValue().getFatalErrorDescription())
        .isEqualTo("Header row does not match expected columns");
  }
}
