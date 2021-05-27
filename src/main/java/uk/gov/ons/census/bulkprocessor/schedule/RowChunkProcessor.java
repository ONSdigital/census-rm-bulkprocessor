package uk.gov.ons.census.bulkprocessor.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRow;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRowStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRowRepository;
import uk.gov.ons.census.bulkprocessor.utility.ObjectMapperFactory;
import uk.gov.ons.census.bulkprocessor.validation.ColumnValidator;

@Component
public class RowChunkProcessor {
  private static final ObjectMapper objectMapper = ObjectMapperFactory.objectMapper();

  private final JobRowRepository jobRowRepository;
  private final JmsTemplate jmsTemplate;

  public RowChunkProcessor(JobRowRepository jobRowRepository, JmsTemplate jmsTemplate) {
    this.jobRowRepository = jobRowRepository;
    this.jmsTemplate = jmsTemplate;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public boolean processChunk(Job job) {
    boolean hadErrors = false;

    List<JobRow> jobRows =
        jobRowRepository.findTop500ByJobAndAndJobRowStatus(job, JobRowStatus.STAGED);

    for (JobRow jobRow : jobRows) {
      JobRowStatus rowStatus = JobRowStatus.PROCESSED_OK;
      List<String> rowValidationErrors = new LinkedList<>();

      for (ColumnValidator columnValidator : job.getBulkProcess().getColumnValidators()) {
        Optional<String> columnValidationErrors = columnValidator.validateRow(jobRow.getRowData());
        if (columnValidationErrors.isPresent()) {
          rowStatus = JobRowStatus.PROCESSED_ERROR;
          rowValidationErrors.add(columnValidationErrors.get());
          hadErrors = true;
        }
      }

      if (rowValidationErrors.size() == 0) {
        jmsTemplate.send(
            job.getBulkProcess().getTargetRoutingKey(),
            s -> {
              try {
                return s.createTextMessage(
                    objectMapper.writeValueAsString(
                        job.getBulkProcess().getTransformer().transformRow(jobRow.getRowData())));
              } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
              }
            });
      }

      jobRow.setValidationErrorDescriptions(String.join(", ", rowValidationErrors));
      jobRow.setJobRowStatus(rowStatus);
    }

    jobRowRepository.saveAll(jobRows);

    return hadErrors;
  }
}
