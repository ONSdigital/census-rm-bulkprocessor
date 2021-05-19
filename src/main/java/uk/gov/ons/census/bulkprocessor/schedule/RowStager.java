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
public class RowStager {
  private final JobRepository jobRepository;
  private final RowChunkStager rowChunkStager;

  public RowStager(JobRepository jobRepository, RowChunkStager rowChunkStager) {
    this.jobRepository = jobRepository;
    this.rowChunkStager = rowChunkStager;
  }

  @Scheduled(fixedDelayString = "1000")
  public void processRows() {
    List<Job> jobs = jobRepository.findByJobStatus(JobStatus.STAGING_IN_PROGRESS);

    for (Job job : jobs) {
      JobStatus jobStatus = JobStatus.FILE_STAGED;

      try (Reader reader = Files.newBufferedReader(Path.of("/tmp/" + job.getFileId()));
          CSVReader csvReader = new CSVReader(reader)) {
        String[] headerRow = csvReader.readNext();

        // Stage all the rows
        while (job.getStagingRowNumber() < job.getFileRowCount() - 1) {
          rowChunkStager.stageChunk(job, headerRow);
        }

        job.setJobStatus(jobStatus);
        jobRepository.saveAndFlush(job);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
