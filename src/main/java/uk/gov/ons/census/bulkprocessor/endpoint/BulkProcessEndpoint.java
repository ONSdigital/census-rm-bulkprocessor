package uk.gov.ons.census.bulkprocessor.endpoint;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.census.bulkprocessor.model.dto.BulkProcessDto;
import uk.gov.ons.census.bulkprocessor.model.entity.BulkProcess;
import uk.gov.ons.census.bulkprocessor.model.entity.User;
import uk.gov.ons.census.bulkprocessor.model.repository.UserRepository;
import uk.gov.ons.census.bulkprocessor.security.UserIdentity;

@RestController
@RequestMapping(value = "/bulkprocess")
public class BulkProcessEndpoint {
  private final UserIdentity userIdentity;
  private final UserRepository userRepository;

  public BulkProcessEndpoint(UserIdentity userIdentity, UserRepository userRepository) {
    this.userIdentity = userIdentity;
    this.userRepository = userRepository;
  }

  @GetMapping
  public List<BulkProcessDto> getBulkProcesses(@RequestHeader HttpHeaders headers) {
    List<BulkProcessDto> bulkProcessDtos = new LinkedList<>();

    String jwtToken = headers.getFirst("x-goog-iap-jwt-assertion");
    String userEmail = userIdentity.getUserEmail(jwtToken);
    Optional<User> userOpt = userRepository.findByEmail(userEmail);

    if (userOpt.isPresent()) {
      userOpt
          .get()
          .getBulkProcesses()
          .forEach(bulkProcess -> bulkProcessDtos.add(mapBulkProcess(bulkProcess)));
    } else {
      // Hack for local testing... return all bulk processors if user is not in DB
      List.of(BulkProcess.values())
          .forEach(bulkProcess -> bulkProcessDtos.add(mapBulkProcess(bulkProcess)));
    }

    return bulkProcessDtos;
  }

  private BulkProcessDto mapBulkProcess(BulkProcess bulkProcess) {
    BulkProcessDto bulkProcessDto = new BulkProcessDto();
    bulkProcessDto.setBulkProcess(bulkProcess.name());
    bulkProcessDto.setTitle(bulkProcess.getTitle());
    return bulkProcessDto;
  }
}
