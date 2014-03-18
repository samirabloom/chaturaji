package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author samirarabbanian
 */
public class GameIntegrationTest extends GameControllerFullIntegrationTest {

    @Test
    public void shouldRetrieveListOfCurrentGamesAndCreateGame() throws Exception {
        // given
        HttpClient httpClient = createApacheClient();

        // --- get list of games ---

        // when
        HttpGet getGames = new HttpGet("https://127.0.0.1:" + httpsPort + "/games");
        HttpResponse getGamesResponse = httpClient.execute(getGames);
        HttpEntity entity = getGamesResponse.getEntity();
        String content = EntityUtils.toString(entity);
        Game[] games = new ObjectMapperFactory().createObjectMapper().readValue(content, Game[].class);

        // then
        assertEquals(numberOfGames, games.length);

        // --- login ---

        // given
        registerUser("user_two@example.com", "qazQAZ123");

        // when
        HttpPost login = new HttpPost("https://user_two%40example.com:qazQAZ123@127.0.0.1:" + httpsPort + "/login");
        HttpResponse loginResponse = httpClient.execute(login);

        // then
        assertEquals(HttpStatus.ACCEPTED.value(), loginResponse.getStatusLine().getStatusCode());

        // --- create game ---

        // when
        HttpPost createGame = new HttpPost("https://127.0.0.1:" + httpsPort + "/createGame");
        createGame.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("numberOfAIPlayers", "3")
        )));
        HttpResponse createGameResponse = httpClient.execute(createGame);

        // then
        assertEquals(HttpStatus.CREATED.value(), createGameResponse.getStatusLine().getStatusCode());
        assertEquals(games.length, numberOfGames());
    }


    @Test
    public void shouldCreateGameAndJoinGame() throws Exception {
        // given
        HttpClient httpClient = createApacheClient();

        // --- login ---

        // given
        registerUser("user_two@example.com", "qazQAZ123");

        // when
        HttpPost login = new HttpPost("https://user_two%40example.com:qazQAZ123@127.0.0.1:" + httpsPort + "/login");
        HttpResponse loginResponse = httpClient.execute(login);

        // then
        assertEquals(HttpStatus.ACCEPTED.value(), loginResponse.getStatusLine().getStatusCode());

        // --- create game ---

        // when
        HttpPost createGame = new HttpPost("https://127.0.0.1:" + httpsPort + "/createGame");
        createGame.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("numberOfAIPlayers", "2")
        )));
        HttpResponse createGameResponse = httpClient.execute(createGame);
        numberOfGames++;

        // then
        assertEquals(HttpStatus.CREATED.value(), createGameResponse.getStatusLine().getStatusCode());
        Player player = new ObjectMapperFactory().createObjectMapper().readValue(EntityUtils.toString(createGameResponse.getEntity()), Player.class);
        assertNotNull(player);
        assertEquals(numberOfGames, numberOfGames());

        // --- logout ---

        // when
        HttpPost logout = new HttpPost("https://127.0.0.1:" + httpsPort + "/logout");
        HttpResponse logoutResponse = httpClient.execute(logout);

        // then
        assertEquals(HttpStatus.FOUND.value(), logoutResponse.getStatusLine().getStatusCode());
        assertEquals("https://127.0.0.1:" + httpsPort + "/", logoutResponse.getFirstHeader("Location").getValue());

        httpClient = createApacheClient();

        // --- login another user ---

        // given
        registerUser("user_three@example.com", "qazQAZ123");

        // when
        login = new HttpPost("https://user_three%40example.com:qazQAZ123@127.0.0.1:" + httpsPort + "/login");
        loginResponse = httpClient.execute(login);

        // then
        assertEquals(HttpStatus.ACCEPTED.value(), loginResponse.getStatusLine().getStatusCode());

        // --- join game ---

        // when
        HttpPost joinGame = new HttpPost("https://127.0.0.1:" + httpsPort + "/joinGame");
        joinGame.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("gameId", player.getGameId())
        )));
        HttpResponse joinGameResponse = httpClient.execute(joinGame);
        numberOfGames--;

        // then
        assertEquals(HttpStatus.CREATED.value(), joinGameResponse.getStatusLine().getStatusCode());
        player = new ObjectMapperFactory().createObjectMapper().readValue(EntityUtils.toString(joinGameResponse.getEntity()), Player.class);
        assertNotNull(player);
    }

    @Test
    public void shouldNotAllowGameCreationIfNotLoggedIn() throws Exception {
        // given
        HttpClient httpClient = createApacheClient();

        // when
        HttpPost createGame = new HttpPost("https://127.0.0.1:" + httpsPort + "/createGame");
        createGame.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("numberOfAIPlayers", "3")
        )));
        HttpResponse createGameResponse = httpClient.execute(createGame);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), createGameResponse.getStatusLine().getStatusCode());
    }
}
