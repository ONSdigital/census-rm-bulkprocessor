package uk.gov.ons.census.bulkprocessor.schedule;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRow;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRowStatus;
import uk.gov.ons.census.bulkprocessor.model.entity.JobStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRepository;
import uk.gov.ons.census.bulkprocessor.validation.ColumnValidator;

@Component
public class RowProcessor {

  private final JobRepository jobRepository;
  private final RabbitTemplate rabbitTemplate;

  public RowProcessor(JobRepository jobRepository, RabbitTemplate rabbitTemplate) {
    this.jobRepository = jobRepository;
    this.rabbitTemplate = rabbitTemplate;
  }

  @Scheduled(fixedDelayString = "1000")
  @Transactional
  public void processStagedJobs() {
    List<Job> jobs = jobRepository.findByJobStatus(JobStatus.FILE_STAGED);

    boolean hadErrors = false;

    for (Job job : jobs) {
      for (JobRow jobRow : job.getJobRows()) {
        JobRowStatus rowStatus = JobRowStatus.PROCESSED_OK;
        List<String> rowValidationErrors = new LinkedList<>();

        for (ColumnValidator columnValidator : job.getBulkProcess().getColumnValidators()) {
          Optional<String> columnValidationErrors =
              columnValidator.validateRow(jobRow.getRowData());
          if (columnValidationErrors.isPresent()) {
            rowStatus = JobRowStatus.PROCESSED_ERROR;
            rowValidationErrors.add(columnValidationErrors.get());
            hadErrors = true;
          }
        }

        if (rowValidationErrors.size() == 0) {
          rabbitTemplate.convertAndSend(
              job.getBulkProcess().getTargetExchange(),
              job.getBulkProcess().getTargetRoutingKey(),
              job.getBulkProcess().getTransformer().transformRow(jobRow.getRowData()));
        }

        jobRow.setValidationErrorDescriptions(String.join(", ", rowValidationErrors));
        jobRow.setJobRowStatus(rowStatus);
      }

      if (hadErrors) {
        job.setJobStatus(JobStatus.PROCESSED_WITH_ERRORS);
      } else {
        job.setJobStatus(JobStatus.PROCESSED_OK);
      }

      jobRepository.saveAndFlush(job);
    }
  }
}
