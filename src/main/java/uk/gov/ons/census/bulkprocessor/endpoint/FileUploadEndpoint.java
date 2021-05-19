package uk.gov.ons.census.bulkprocessor.endpoint;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.ons.census.bulkprocessor.model.entity.BulkProcess;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRepository;

@Controller
public class FileUploadEndpoint {

  private final JobRepository jobRepository;

  public FileUploadEndpoint(JobRepository jobRepository) {
    this.jobRepository = jobRepository;
  }

  @PostMapping("/upload")
  public ResponseEntity<?> handleFileUpload(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "bulkProcess", required = false, defaultValue = "REFUSAL")
          BulkProcess bulkProcess) {
    UUID fileId = UUID.randomUUID();

    try (FileOutputStream fos = new FileOutputStream("/tmp/" + fileId)) {
      Job job = new Job();
      job.setId(UUID.randomUUID());

      job.setBulkProcess(bulkProcess);
      job.setFileName(file.getOriginalFilename());
      job.setFileId(fileId);
      job.setJobStatus(JobStatus.FILE_UPLOADED);

      fos.write(file.getBytes());

      jobRepository.saveAndFlush(job);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new ResponseEntity<>(null, HttpStatus.CREATED);
  }
}
