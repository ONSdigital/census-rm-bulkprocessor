package uk.gov.ons.census.bulkprocessor.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.ons.census.bulkprocessor.model.entity.BulkProcess.NEW_ADDRESS;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.census.bulkprocessor.model.dto.JobDto;
import uk.gov.ons.census.bulkprocessor.model.dto.JobStatusDto;
import uk.gov.ons.census.bulkprocessor.model.entity.BulkProcess;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRepository;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRowRepository;
import uk.gov.ons.census.bulkprocessor.models.test_dtos.CollectionCase;
import uk.gov.ons.census.bulkprocessor.models.test_dtos.ResponseManagementEvent;
import uk.gov.ons.census.bulkprocessor.testutils.QueueSpy;
import uk.gov.ons.census.bulkprocessor.testutils.RabbitQueueHelper;

@ContextConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileUploadEndPointIT {
  private static final String NEW_ADDRESS_FILE = "new_addresses.csv";
  private static final String TEST_FILES_DIR = "test_files/";

  @Autowired JobRepository jobRepository;

  @Autowired JobRowRepository jobRowRepository;

  @Autowired private RabbitQueueHelper rabbitQueueHelper;

  private String outputQueue = "case.rh.case";
  String fileUploadUrl = "";

  RestTemplate restTemplate = new RestTemplate();

  @LocalServerPort private int port;

  @Before
  public void setUp() {
    fileUploadUrl = "http://localhost:" + port + "/upload/";
    rabbitQueueHelper.purgeQueue(outputQueue);
    jobRepository.deleteAll();
  }

  @Test
  public void UploadANewAddressFile() throws Exception {

    try (QueueSpy outputQueueSpy = rabbitQueueHelper.listen(outputQueue)) {
      HttpEntity<MultiValueMap<String, Object>> requestEntity =
          buildEntityToSendFile(NEW_ADDRESS_FILE, NEW_ADDRESS);
      ResponseEntity<String> response =
          restTemplate.postForEntity(fileUploadUrl, requestEntity, String.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

      ResponseManagementEvent emittedCase = outputQueueSpy.checkExpectedMessageReceived();
      assertThat(emittedCase.getPayload().getCollectionCase().getAddress().getUprn())
          .isEqualTo("6034022");
      assertThat(emittedCase.getPayload().getCollectionCase().getAddress().getOrganisationName())
          .isEqualTo("TEAM RM");

      JobDto job = getSingleJobAndCheckTheresOnlyOne(NEW_ADDRESS);
      assertThat(job.getJobStatus()).isEqualTo(JobStatusDto.PROCESSED_OK);

      assertThat(job.getStagedRowCount()).isEqualTo(1);
      assertThat(job.getFileName()).isEqualTo(NEW_ADDRESS_FILE);
      assertThat(job.getBulkProcess()).isEqualTo("NEW_ADDRESS");
      assertThat(job.getFileRowCount()).isEqualTo(2);
    }
  }

  @Test
  public void UploadARefusalFile() throws InterruptedException, Exception {
    CollectionCase emittedCase;

    // 1st load a New Address to get a case to refuse
    try (QueueSpy outputQueueSpy = rabbitQueueHelper.listen(outputQueue)) {
      HttpEntity<MultiValueMap<String, Object>> requestEntity =
          buildEntityToSendFile(NEW_ADDRESS_FILE, NEW_ADDRESS);

      ResponseEntity<String> response =
          restTemplate.postForEntity(fileUploadUrl, requestEntity, String.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

      emittedCase = outputQueueSpy.checkExpectedMessageReceived().getPayload().getCollectionCase();
      assertThat(emittedCase.getAddress().getUprn()).isEqualTo("6034022");

      JobDto job = getSingleJobAndCheckTheresOnlyOne(NEW_ADDRESS);
      assertThat(job.getJobStatus()).isEqualTo(JobStatusDto.PROCESSED_OK);
    }

    try (QueueSpy outtyQueue = rabbitQueueHelper.listen(outputQueue)) {
      HttpEntity<MultiValueMap<String, Object>> entityToSend =
          buildEntityForRefusal(emittedCase.getId());
      ResponseEntity<String> response =
          restTemplate.postForEntity(fileUploadUrl, entityToSend, String.class);
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

      CollectionCase refusalEmittedCaseUpdate =
          outtyQueue.checkExpectedMessageReceived().getPayload().getCollectionCase();
      assertThat(refusalEmittedCaseUpdate.getId()).isEqualTo(emittedCase.getId());

      assertThat(refusalEmittedCaseUpdate.getRefusalReceived().name())
          .isEqualTo("EXTRAORDINARY_REFUSAL");
    }
  }

  private JobDto getSingleJobAndCheckTheresOnlyOne(BulkProcess bulkProcess) {
    String allJobsUrl = "http://localhost:" + port + "/job?bulkProcess=" + bulkProcess.name();
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<JobDto[]> allJobsResponse =
        restTemplate.getForEntity(allJobsUrl, JobDto[].class);
    assertThat(allJobsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    JobDto[] allJobs = allJobsResponse.getBody();
    assertThat(allJobs.length).isEqualTo(1);

    return allJobs[0];
  }

  private HttpEntity<MultiValueMap<String, Object>> buildEntityForRefusal(UUID caseId)
      throws IOException {
    String fileData = "case_id,refusal_type\n" + caseId + ",EXTRAORDINARY_REFUSAL";
    Path tempFile = Files.createTempFile(null, null);
    System.out.println(tempFile);

    Files.write(tempFile, fileData.getBytes(StandardCharsets.UTF_8));
    FileSystemResource fileSystemResource = new FileSystemResource(tempFile);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", fileSystemResource);
    body.add("bulkProcess", BulkProcess.REFUSAL.toString());

    return new HttpEntity<>(body, headers);
  }

  private HttpEntity<MultiValueMap<String, Object>> buildEntityToSendFile(
      String fileName, BulkProcess bulkProcess) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", new ClassPathResource(TEST_FILES_DIR + fileName));
    body.add("bulkProcess", bulkProcess.toString());

    return new HttpEntity<>(body, headers);
  }
}
