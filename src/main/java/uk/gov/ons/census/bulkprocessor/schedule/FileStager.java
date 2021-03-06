package uk.gov.ons.census.bulkprocessor.schedule;

import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
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
      JobStatus jobStatus = JobStatus.STAGING_IN_PROGRESS;

      try (Reader reader = Files.newBufferedReader(Path.of("/tmp/" + job.getFileId()));
          CSVReader csvReader = new CSVReader(reader)) {

        // Validate the header row has the right number of columns
        String[] headerRow = csvReader.readNext();
        if (headerRow.length != job.getBulkProcess().getExpectedColumns().length) {
          // The header row doesn't have enough columns
          jobStatus = JobStatus.PROCESSED_TOTAL_FAILURE;
          job.setFatalErrorDescription("Header row does not have expected number of columns");
        } else {
          // Validate that the header rows are correct
          for (int index = 0; index < headerRow.length; index++) {
            if (!headerRow[index].equals(job.getBulkProcess().getExpectedColumns()[index])) {
              // The header row doesn't match what we expected
              jobStatus = JobStatus.PROCESSED_TOTAL_FAILURE;
              job.setFatalErrorDescription("Header row does not match expected columns");
            }
          }
        }

        job.setJobStatus(jobStatus);
        jobRepository.saveAndFlush(job);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
