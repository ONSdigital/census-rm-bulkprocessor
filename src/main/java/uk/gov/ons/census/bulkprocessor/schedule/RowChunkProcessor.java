package uk.gov.ons.census.bulkprocessor.schedule;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRow;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRowStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRowRepository;
import uk.gov.ons.census.bulkprocessor.validation.ColumnValidator;

@Component
public class RowChunkProcessor {
  private final JobRowRepository jobRowRepository;
  private final RabbitTemplate rabbitTemplate;

  public RowChunkProcessor(JobRowRepository jobRowRepository, RabbitTemplate rabbitTemplate) {
    this.jobRowRepository = jobRowRepository;
    this.rabbitTemplate = rabbitTemplate;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public boolean processChunk(Job job) {
    AtomicBoolean hadErrors = new AtomicBoolean(false);

    try (Stream<JobRow> jobRows =
        jobRowRepository.findTop500ByJobAndAndJobRowStatus(job, JobRowStatus.STAGED)) {

      jobRows.forEach(
          jobRow -> {
            JobRowStatus rowStatus = JobRowStatus.PROCESSED_OK;
            List<String> rowValidationErrors = new LinkedList<>();

            for (ColumnValidator columnValidator : job.getBulkProcess().getColumnValidators()) {
              Optional<String> columnValidationErrors =
                  columnValidator.validateRow(jobRow.getRowData());
              if (columnValidationErrors.isPresent()) {
                rowStatus = JobRowStatus.PROCESSED_ERROR;
                rowValidationErrors.add(columnValidationErrors.get());
                hadErrors.set(true);
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
            jobRowRepository.save(jobRow);
          });

      jobRowRepository.flush();
    }

    return hadErrors.get();
  }
}
