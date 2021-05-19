package uk.gov.ons.census.bulkprocessor.schedule;

import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRowStatus;
import uk.gov.ons.census.bulkprocessor.model.entity.JobStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRepository;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRowRepository;

@Component
public class StagedJobProcessor {

  private final JobRepository jobRepository;
  private final JobRowRepository jobRowRepository;
  private final RowChunkProcessor rowChunkProcessor;

  public StagedJobProcessor(
      JobRepository jobRepository,
      JobRowRepository jobRowRepository,
      RowChunkProcessor rowChunkProcessor) {
    this.jobRepository = jobRepository;
    this.jobRowRepository = jobRowRepository;
    this.rowChunkProcessor = rowChunkProcessor;
  }

  @Scheduled(fixedDelayString = "1000")
  @Transactional
  public void processStagedJobs() {
    List<Job> jobs = jobRepository.findByJobStatus(JobStatus.FILE_STAGED);

    for (Job job : jobs) {
      JobStatus jobStatus = JobStatus.PROCESSED_OK;

      while (jobRowRepository.existsByJobAndAndJobRowStatus(job, JobRowStatus.STAGED)) {
        if (rowChunkProcessor.processChunk(job)) {
          jobStatus = JobStatus.PROCESSED_WITH_ERRORS;
        }
      }

      job.setJobStatus(jobStatus);
      jobRepository.saveAndFlush(job);
    }
  }
}
