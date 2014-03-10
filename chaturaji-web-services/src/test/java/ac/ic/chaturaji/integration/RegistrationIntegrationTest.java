package ac.ic.chaturaji.integration;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author samirarabbanian
 */
public class RegistrationIntegrationTest extends GameControllerFullIntegrationTest {

    @Test
    public void shouldRegisterUser() throws Exception {
        // given
        HttpClient httpClient = createApacheClient();

        // --- does not login ---

        // when
        HttpPost login = new HttpPost("https://some_user%40example.com:some_password@127.0.0.1:" + httpsPort + "/login");
        HttpResponse loginResponse = httpClient.execute(login);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), loginResponse.getStatusLine().getStatusCode());

        // --- register ---

        // when
        HttpPost register = new HttpPost("https://127.0.0.1:" + httpsPort + "/register");
        register.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("email", "some_user@example.com"),
                new BasicNameValuePair("password", "some_password"),
                new BasicNameValuePair("nickname", "my_nickname")
        )));
        HttpResponse createGameResponse = httpClient.execute(register);

        // then
        assertEquals(HttpStatus.CREATED.value(), createGameResponse.getStatusLine().getStatusCode());

        // --- login ---

        // todo understand why we need to create another client to prevent the code from hanging
        httpClient = createApacheClient();

        // when
        login = new HttpPost("https://some_user%40example.com:some_password@127.0.0.1:" + httpsPort + "/login");
        loginResponse = httpClient.execute(login);

        // then
        assertEquals(HttpStatus.ACCEPTED.value(), loginResponse.getStatusLine().getStatusCode());
    }

}
