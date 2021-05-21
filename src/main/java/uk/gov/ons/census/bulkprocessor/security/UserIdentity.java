package uk.gov.ons.census.bulkprocessor.security;

import com.google.api.client.json.webtoken.JsonWebToken;
import com.google.auth.oauth2.TokenVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UserIdentity {
  private static final String IAP_ISSUER_URL = "https://cloud.google.com/iap";

  private TokenVerifier tokenVerifier = null;

  @Value("${iapaudience}")
  private String iapAudience;

  public String getUserEmail(String jwtToken) {
    if (StringUtils.isEmpty(jwtToken)) {
      // This should throw an exception if we're running in GCP
      // We are faking the email address so that we can test locally
      return "dummy@fake-email.com";
    } else {
      return verifyJwtAndGetEmail(jwtToken);
    }
  }

  private synchronized TokenVerifier getTokenVerifier() {

    if (tokenVerifier == null) {
      tokenVerifier =
          TokenVerifier.newBuilder().setAudience(iapAudience).setIssuer(IAP_ISSUER_URL).build();
    }

    return tokenVerifier;
  }

  private String verifyJwtAndGetEmail(String jwtToken) {
    try {
      TokenVerifier tokenVerifier = getTokenVerifier();
      JsonWebToken jsonWebToken = tokenVerifier.verify(jwtToken);

      // Verify that the token contain subject and email claims
      JsonWebToken.Payload payload = jsonWebToken.getPayload();
      if (payload.getSubject() != null && payload.get("email") != null) {
        return (String) payload.get("email");
      } else {
        return null;
      }
    } catch (TokenVerifier.VerificationException e) {
      throw new RuntimeException(e);
    }
  }
}
