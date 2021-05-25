package uk.gov.ons.census.bulkprocessor.model.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.ons.census.bulkprocessor.model.entity.Job;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRow;
import uk.gov.ons.census.bulkprocessor.model.entity.JobRowStatus;

public interface JobRowRepository extends JpaRepository<JobRow, UUID> {
  int countByJobAndAndJobRowStatus(Job job, JobRowStatus jobRowStatus);

  boolean existsByJobAndAndJobRowStatus(Job job, JobRowStatus jobRowStatus);

  List<JobRow> findByJobAndAndJobRowStatus(Job job, JobRowStatus jobRowStatus);

  List<JobRow> findTop500ByJobAndAndJobRowStatus(Job job, JobRowStatus jobRowStatus);
}
