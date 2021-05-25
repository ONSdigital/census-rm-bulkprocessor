package uk.gov.ons.census.bulkprocessor.schedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRowStatus;
import uk.gov.ons.census.bulkprocessor.model.entity.JobStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRepository;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRowRepository;

@RunWith(MockitoJUnitRunner.class)
public class StagedJobProcessorTest {
  @Mock private JobRepository jobRepository;

  @Mock private JobRowRepository jobRowRepository;

  @Mock private RowChunkProcessor rowChunkProcessor;

  @InjectMocks private StagedJobProcessor underTest;

  @Test
  public void testProcessRows() {
    Job job = new Job();

    when(jobRepository.findByJobStatus(JobStatus.PROCESSING_IN_PROGRESS)).thenReturn(List.of(job));
    when(jobRowRepository.existsByJobAndAndJobRowStatus(job, JobRowStatus.STAGED))
        .thenReturn(true)
        .thenReturn(true)
        .thenReturn(false);
    when(rowChunkProcessor.processChunk(job)).thenReturn(false);

    underTest.processStagedJobs();

    verify(rowChunkProcessor, times(2)).processChunk(job);

    ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
    verify(jobRepository).saveAndFlush(jobArgumentCaptor.capture());

    assertThat(jobArgumentCaptor.getValue()).isEqualTo(job);
    assertThat(jobArgumentCaptor.getValue().getJobStatus()).isEqualTo(JobStatus.PROCESSED_OK);
  }

  @Test
  public void testProcessRowsWithErrors() {
    Job job = new Job();

    when(jobRepository.findByJobStatus(JobStatus.PROCESSING_IN_PROGRESS)).thenReturn(List.of(job));
    when(jobRowRepository.existsByJobAndAndJobRowStatus(job, JobRowStatus.STAGED))
        .thenReturn(true)
        .thenReturn(true)
        .thenReturn(false);
    when(rowChunkProcessor.processChunk(job)).thenReturn(false).thenReturn(true);

    underTest.processStagedJobs();

    verify(rowChunkProcessor, times(2)).processChunk(job);

    ArgumentCaptor<Job> jobArgumentCaptor = ArgumentCaptor.forClass(Job.class);
    verify(jobRepository).saveAndFlush(jobArgumentCaptor.capture());

    assertThat(jobArgumentCaptor.getValue()).isEqualTo(job);
    assertThat(jobArgumentCaptor.getValue().getJobStatus())
        .isEqualTo(JobStatus.PROCESSED_WITH_ERRORS);
  }
}
