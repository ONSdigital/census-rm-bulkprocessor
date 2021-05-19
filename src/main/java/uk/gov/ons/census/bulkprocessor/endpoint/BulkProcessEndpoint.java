package uk.gov.ons.census.bulkprocessor.endpoint;

import java.util.LinkedList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.census.bulkprocessor.model.dto.BulkProcessDto;
import uk.gov.ons.census.bulkprocessor.model.entity.BulkProcess;

@RestController
@RequestMapping(value = "/bulkprocess")
public class BulkProcessEndpoint {

  @GetMapping
  public List<BulkProcessDto> getBulkProcesses() {
    List<BulkProcessDto> bulkProcessDtos = new LinkedList<>();

    for (BulkProcess bulkProcess : BulkProcess.values()) {
      BulkProcessDto bulkProcessDto = new BulkProcessDto();
      bulkProcessDto.setBulkProcess(bulkProcess.name());
      bulkProcessDto.setTitle(bulkProcess.getTitle());
      bulkProcessDtos.add(bulkProcessDto);
    }

    return bulkProcessDtos;
  }
}
