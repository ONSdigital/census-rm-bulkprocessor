package uk.gov.ons.census.bulkprocessor.endpoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.census.bulkprocessor.model.dto.JobDto;
import uk.gov.ons.census.bulkprocessor.model.dto.JobStatusDto;
import uk.gov.ons.census.bulkprocessor.model.entity.BulkProcess;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRow;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRowStatus;
import uk.gov.ons.census.bulkprocessor.model.entity.JobStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRepository;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRowRepository;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobEndPointIT {
    @Autowired
    JobRepository jobRepository;

    @Autowired
    JobRowRepository jobRowRepository;

    String jobUrl = "";

    RestTemplate restTemplate = new RestTemplate();

    @LocalServerPort
    private int port;

    @Before
    public void setUp() {
        jobUrl = "http://localhost:" + port + "/job";
        jobRepository.deleteAll();
    }


    @Test
    public void findAllJobsIT()  {
        for( int i = 0; i < 10; i++) {
            Job job = new Job();
            job.setId(UUID.randomUUID());
            job.setBulkProcess(BulkProcess.REFUSAL);
            job.setJobStatus(JobStatus.PROCESSED_OK);
            jobRepository.saveAndFlush(job);
        }

        String url = jobUrl + "?bulkProcess=" + BulkProcess.REFUSAL.toString();
        ResponseEntity<JobDto[]> actualJobsResponse = restTemplate.getForEntity(url, JobDto[].class);
        assertThat(actualJobsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JobDto[] actualJobs = actualJobsResponse.getBody();
        assertThat(actualJobs.length).isEqualTo(10);
    }

    @Test
    public void testGetJobByIdAndCheckFieldsPopulatedCorrectly() {
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setBulkProcess(BulkProcess.REFUSAL);
        job.setJobStatus(JobStatus.PROCESSED_OK);

        job.setCreatedAt(OffsetDateTime.now().minusDays(1));
        job.setCreatedBy("NDD");
        job.setLastUpdatedAt(OffsetDateTime.now());
        job.setFileName("A_refusal_file.csv");
        job.setFileRowCount(137);

        jobRepository.saveAndFlush(job);

        ResponseEntity<JobDto> actualJobResponse = restTemplate.getForEntity(jobUrl + "/" + job.getId().toString(), JobDto.class);
        assertThat(actualJobResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JobDto actualJob = actualJobResponse.getBody();
        assertThat(actualJob.getId()).isEqualTo(job.getId());
        assertThat(actualJob.getJobStatus().toString()).isEqualTo(job.getJobStatus().toString());
        assertThat(actualJob.getCreatedAt()).isEqualTo(job.getCreatedAt());
        assertThat(actualJob.getCreatedBy()).isEqualTo("NDD");
        assertThat(actualJob.getLastUpdatedAt()).isEqualTo(job.getLastUpdatedAt());
        assertThat(actualJob.getFileName()).isEqualTo("A_refusal_file.csv");
        assertThat(actualJob.getFileRowCount()).isEqualTo(137);
    }

    @Test
    public void getErrorDetails() {
        Job job = new Job();
        job.setId(UUID.randomUUID());
        jobRepository.saveAndFlush(job);

        JobRow jobRow1 = new JobRow();
        jobRow1.setId(UUID.randomUUID());
        jobRow1.setJob(job);
        jobRow1.setJobRowStatus(JobRowStatus.PROCESSED_ERROR);
        jobRow1.setOriginalRowLineNumber(11);
        jobRow1.setValidationErrorDescriptions("Bad Speling");
        Map<String, String> jobRowData = new HashMap<>();
        jobRowData.put("key1", "bad data 1");

        jobRowRepository.saveAndFlush(jobRow1);

        JobRow jobRow2 = new JobRow();
        jobRow2.setId(UUID.randomUUID());
        jobRow2.setJob(job);
        jobRow2.setJobRowStatus(JobRowStatus.PROCESSED_ERROR);
        jobRow2.setOriginalRowLineNumber(111);
        jobRow2.setValidationErrorDescriptions("Grammar bad");
        Map<String, String> jobRowData2 = new HashMap<>();
        jobRowData2.put("key2", "bad data 2");
        jobRowRepository.saveAndFlush(jobRow2);

        ResponseEntity<String> errorStringResponse = restTemplate.getForEntity(jobUrl + "/" + job.getId().toString() + "/errorDetail", String.class);
        assertThat(errorStringResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(errorStringResponse.getBody()).isEqualTo("\"ORIGINAL ROW NUMBER\",\"ERRORS\"\n\"11\",\"Bad Speling\"\n\"111\",\"Grammar bad\"\n");
    }

    @Test
    public void getErrors() {
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setBulkProcess(BulkProcess.REFUSAL);
        jobRepository.saveAndFlush(job);

        JobRow jobRow1 = new JobRow();
        jobRow1.setId(UUID.randomUUID());
        jobRow1.setJob(job);
        jobRow1.setJobRowStatus(JobRowStatus.PROCESSED_ERROR);
        UUID jobRow1Id = UUID.randomUUID();
        jobRow1.setOriginalRowData(new String[]{jobRow1Id.toString(), "DIDN'T FANCY IT"});
        jobRowRepository.saveAndFlush(jobRow1);

        JobRow jobRow2 = new JobRow();
        jobRow2.setId(UUID.randomUUID());
        jobRow2.setJob(job);
        jobRow2.setJobRowStatus(JobRowStatus.PROCESSED_ERROR);
        UUID jobRow2Id = UUID.randomUUID();
        jobRow2.setOriginalRowData(new String[]{jobRow2Id.toString(), "HAD TO WASH HAIR INSTEAD"});
        jobRowRepository.saveAndFlush(jobRow2);

        ResponseEntity<String> errorStringResponse = restTemplate.getForEntity(jobUrl + "/" + job.getId().toString() + "/error", String.class);

        assertThat(errorStringResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(errorStringResponse.getBody()).isEqualTo("\"case_id\",\"refusal_type\"\n"
                + "\"" + jobRow1Id.toString() + "\",\"DIDN'T FANCY IT\"\n"
                + "\"" + jobRow2Id.toString() + "\",\"HAD TO WASH HAIR INSTEAD\"\n");
    }
}
