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
import uk.gov.ons.census.bulkprocessor.model.entity.BulkProcess;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobStatus;
import uk.gov.ons.census.bulkprocessor.model.repository.JobRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobEndPointIT {
    private static final String NEW_ADDRESS_FILE = "new_addresses.csv";
    private static final String TEST_FILES_DIR = "test_files/";

    @Autowired
    JobRepository jobRepository;
//
//    @Autowired
//    JobRowRepository jobRowRepository;

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

        ResponseEntity<JobDto[]> actualJobsResponse = restTemplate.getForEntity(jobUrl, JobDto[].class);
        assertThat(actualJobsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        JobDto[] actualJobs = actualJobsResponse.getBody();
        assertThat(actualJobs.length).isEqualTo(10);
    }

    @Test
    public void testGetJobById() {
        Job job = new Job();
        job.setId(UUID.randomUUID());
        job.setBulkProcess(BulkProcess.REFUSAL);
        job.setJobStatus(JobStatus.PROCESSED_OK);

        jobRepository.saveAndFlush(job);

        ResponseEntity<JobDto> actualJobResponse = restTemplate.getForEntity(jobUrl + "/" + job.getId().toString(), JobDto.class);
        assertThat(actualJobResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actualJobResponse.getBody().getId()).isEqualTo(job.getId());
    }
}
