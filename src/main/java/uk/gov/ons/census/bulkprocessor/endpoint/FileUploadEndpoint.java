package uk.gov.ons.census.bulkprocessor.endpoint;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.census.bulkprocessor.model.entity.BulkProcess;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRepository;
import uk.gov.ons.census.bulkprocessor.security.UserIdentity;

@Controller
public class FileUploadEndpoint {

  private final JobRepository jobRepository;
  private final UserIdentity userIdentity;

  public FileUploadEndpoint(JobRepository jobRepository, UserIdentity userIdentity) {
    this.jobRepository = jobRepository;
    this.userIdentity = userIdentity;
  }

  @PostMapping("/upload")
  public ResponseEntity<?> handleFileUpload(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "bulkProcess", required = false, defaultValue = "REFUSAL")
          BulkProcess bulkProcess,
      @RequestHeader(required = false, value = "x-goog-iap-jwt-assertion") String jwtToken) {
    if (!userIdentity.getBulkProcesses(jwtToken).contains(bulkProcess)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorised");
    }

    UUID fileId = UUID.randomUUID();

    try (FileOutputStream fos = new FileOutputStream("/tmp/" + fileId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      Job job = new Job();
      job.setId(UUID.randomUUID());

      job.setBulkProcess(bulkProcess);
      job.setFileName(file.getOriginalFilename());
      job.setFileId(fileId);
      job.setJobStatus(JobStatus.FILE_UPLOADED);
      job.setCreatedBy(userIdentity.getUserEmail(jwtToken));

      int rowCount = 0;
      while (reader.ready()) {
        String line = reader.readLine();
        rowCount++;
        fos.write(line.getBytes());
        fos.write("\n".getBytes());
      }

      job.setFileRowCount(rowCount);

      jobRepository.saveAndFlush(job);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new ResponseEntity<>(null, HttpStatus.CREATED);
  }
}
