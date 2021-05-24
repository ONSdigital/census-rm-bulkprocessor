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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRow;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRowStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRepository;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRowRepository;

@Component
public class RowChunkStager {
  private final int CHUNK_SIZE = 500;
  private final JobRepository jobRepository;
  private final JobRowRepository jobRowRepository;

  public RowChunkStager(JobRepository jobRepository, JobRowRepository jobRowRepository) {
    this.jobRepository = jobRepository;
    this.jobRowRepository = jobRowRepository;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void stageChunk(Job job, String[] headerRow) {
    try (Reader reader = Files.newBufferedReader(Path.of("/tmp/" + job.getFileId()));
        CSVReader csvReader = new CSVReader(reader)) {
      List<JobRow> jobRows = new LinkedList<>();

      // Skip lines which we don't need, until we reach progress point... including header row
      for (int i = 0; i < job.getStagingRowNumber() + 1; i++) {
        csvReader.readNext();
      }

      for (int i = 0; i < CHUNK_SIZE; i++) {
        String[] line = csvReader.readNext();
        if (line == null) {
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

        job.setStagingRowNumber(job.getStagingRowNumber() + 1);
        jobRow.setOriginalRowLineNumber(job.getStagingRowNumber());
        jobRows.add(jobRow);
      }

      jobRepository.saveAndFlush(job);
      jobRowRepository.saveAll(jobRows);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
