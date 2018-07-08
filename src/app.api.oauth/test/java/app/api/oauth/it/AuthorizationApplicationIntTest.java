package app.api.oauth.it;

import app.api.oauth.AuthorizationApplication;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuthorizationApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthorizationApplicationIntTest {

    @Autowired
    private JwtTokenStore tokenStore;

    @LocalServerPort
    private int localPort;

    @Test
    public void getAuthenticationPrincipal() {
        String accessToken = getAccessToken("user", "user", "john", "123");
        OAuth2Authentication auth = tokenStore.readAuthentication(accessToken);
        assertThat(auth.getPrincipal(), is("john"));
    }

    private String getAccessToken(String clientId, String clientSecret, String username, String password) {
        Map<String,String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("client_id", clientId);
        params.put("grant_type", "password");
        String tokenUrl = String.format("https://localhost:%s/oauth/token", localPort);
        Response response = RestAssured.given()
                .relaxedHTTPSValidation()
                .auth().preemptive().basic(clientId, clientSecret)
                .and().with().params(params)
                .when().post(tokenUrl);
        return response.jsonPath().getString("access_token");
    }

}
