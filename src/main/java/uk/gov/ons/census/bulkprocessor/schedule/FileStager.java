package uk.gov.ons.census.bulkprocessor.schedule;

import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRow;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRowStatus;
import uk.gov.ons.census.bulkprocessor.model.entity.JobStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRepository;

@Component
public class FileStager {

  private final JobRepository jobRepository;

  public FileStager(JobRepository jobRepository) {
    this.jobRepository = jobRepository;
  }

  @Scheduled(fixedDelayString = "1000")
  public void processFiles() {
    List<Job> jobs = jobRepository.findByJobStatus(JobStatus.FILE_UPLOADED);

    for (Job job : jobs) {
      JobStatus jobStatus = JobStatus.FILE_STAGED;

      try (Reader reader = Files.newBufferedReader(Path.of("/tmp/" + job.getFileId()));
          CSVReader csvReader = new CSVReader(reader)) {

        // Validate the header row has the right number of columns
        String[] headerRow = csvReader.readNext();
        if (headerRow.length != job.getBulkProcess().getExpectedColumns().length) {
          // The header row doesn't have enough columns
          jobStatus = JobStatus.PROCESSED_TOTAL_FAILURE;
          job.setFatalErrorDescription("Header row does not have expected number of columns");
        }

        // Validate that the header rows are correct
        if (jobStatus == JobStatus.FILE_STAGED) {
          for (int index = 0; index < headerRow.length; index++) {
            if (!headerRow[index].equals(job.getBulkProcess().getExpectedColumns()[index])) {
              // The header row doesn't match what we expected
              jobStatus = JobStatus.PROCESSED_TOTAL_FAILURE;
              job.setFatalErrorDescription("Header row does not match expected columns");
            }
          }
        }

        // If we passed earlier validation, go ahead and stage every row
        if (jobStatus == JobStatus.FILE_STAGED) {
          jobStatus = stageRows(csvReader, headerRow, job);
        }

        job.setJobStatus(jobStatus);
        jobRepository.saveAndFlush(job);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private JobStatus stageRows(CSVReader csvReader, String[] headerRow, Job job) throws IOException {
    JobStatus jobStatus = JobStatus.FILE_STAGED;

    int lineNumber = 1;
    String[] line;
    List<JobRow> jobRows = new LinkedList<>();
    while ((line = csvReader.readNext()) != null) {
      if (line.length != headerRow.length) {
        // There are rows which have a different number of columns from the header row
        jobStatus = JobStatus.PROCESSED_TOTAL_FAILURE;
        job.setFatalErrorDescription("There is at least 1 row with wrong number of columns");
        break;
      }

      Map<String, String> rowData = new HashMap<>();

      JobRow jobRow = new JobRow();
      jobRow.setId(UUID.randomUUID());
      jobRow.setJob(job);
      jobRow.setJobRowStatus(JobRowStatus.STAGED);

      for (int index = 0; index < line.length; index++) {
        rowData.put(headerRow[index], line[index]);
      }

      jobRow.setRowData(rowData);
      jobRow.setOriginalRowData(line);
      jobRow.setOriginalRowLineNumber(lineNumber++);
      jobRows.add(jobRow);
    }

    if (jobStatus == JobStatus.FILE_STAGED) {
      job.setJobRows(jobRows);
    }

    return jobStatus;
  }
}
