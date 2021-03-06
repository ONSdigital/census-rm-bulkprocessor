package uk.gov.ons.census.bulkprocessor.endpoint;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.census.bulkprocessor.security.UserIdentity;

@RestController
@RequestMapping(value = "/whoami")
public class WhoAmI {
  private final UserIdentity userIdentity;

  public WhoAmI(UserIdentity userIdentity) {
    this.userIdentity = userIdentity;
  }

  @GetMapping
  public Map<String, String> getWhoIAm(
      @RequestHeader(required = false, value = "x-goog-iap-jwt-assertion") String jwtToken) {
    String userEmail = userIdentity.getUserEmail(jwtToken);

    return Map.of("user", userEmail);
  }
}
