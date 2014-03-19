package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.model.Colour;
import ac.ic.chaturaji.model.Move;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.Result;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import ac.ic.chaturaji.uuid.UUIDFactory;
import ac.ic.chaturaji.websockets.GameMoveListener;
import ac.ic.chaturaji.websockets.WebSocketsClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author samirarabbanian
 */
public class WebSocketIntegrationTest extends GameControllerFullIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapperFactory().createObjectMapper();
    private UUIDFactory uuidFactory = new UUIDFactory();

    @Test
    public void shouldCreateGameAndJoinGame() throws Exception {
        // given
        HttpClient httpClient = createApacheClient();
        GameMoveListener[] mockGameMoveListeners = new GameMoveListener[]{
                mock(GameMoveListener.class),
                mock(GameMoveListener.class),
                mock(GameMoveListener.class),
                mock(GameMoveListener.class)
        };
        WebSocketsClient webSocketsClient = new WebSocketsClient("127.0.0.1");

        // --- register four users ---

        for (int i = 0; i < 4; i++) {
            registerUser("socket_user_" + i + "@email.com", "socket_password_" + i);
        }

        // --- login ---

        // when
        HttpPost login = new HttpPost("https://socket_user_0%40email.com:socket_password_0@127.0.0.1:" + httpsPort + "/login");
        HttpResponse loginResponse = httpClient.execute(login);

        // then
        assertEquals(HttpStatus.ACCEPTED.value(), loginResponse.getStatusLine().getStatusCode());

        // --- create game ---

        // when
        HttpPost createGame = new HttpPost("https://127.0.0.1:" + httpsPort + "/createGame");
        createGame.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                new BasicNameValuePair("numberOfAIPlayers", "0")
        )));
        HttpResponse createGameResponse = httpClient.execute(createGame);
        numberOfGames++;

        // then
        assertEquals(HttpStatus.CREATED.value(), createGameResponse.getStatusLine().getStatusCode());
        Player player = objectMapper.readValue(EntityUtils.toString(createGameResponse.getEntity()), Player.class);
        assertEquals(numberOfGames, numberOfGames());

        // --- connect web socket ---

        webSocketsClient.registerGameMoveListener(mockGameMoveListeners[0], player.getId());

        // --- logout ---

        // when
        HttpPost logout = new HttpPost("https://127.0.0.1:" + httpsPort + "/logout");
        HttpResponse logoutResponse = httpClient.execute(logout);

        // then
        assertEquals(HttpStatus.FOUND.value(), logoutResponse.getStatusLine().getStatusCode());
        assertEquals("https://127.0.0.1:" + httpsPort + "/", logoutResponse.getFirstHeader("Location").getValue());

        // --- login and join game ---

        for (int i = 1; i < 4; i++) {
            httpClient = createApacheClient();

            // when
            login = new HttpPost("https://socket_user_" + i + "%40email.com:socket_password_" + i + "@127.0.0.1:" + httpsPort + "/login");
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

            // then
            assertEquals(HttpStatus.CREATED.value(), joinGameResponse.getStatusLine().getStatusCode());
            player = objectMapper.readValue(EntityUtils.toString(joinGameResponse.getEntity()), Player.class);

            // --- connect web socket ---

            webSocketsClient.registerGameMoveListener(mockGameMoveListeners[i], player.getId());

            if (i < 3) { // do not logout last user so they can submit move

                // --- logout ---

                // when
                logout = new HttpPost("https://127.0.0.1:" + httpsPort + "/logout");
                logoutResponse = httpClient.execute(logout);

                // then
                assertEquals(HttpStatus.FOUND.value(), logoutResponse.getStatusLine().getStatusCode());
            }
        }

        // wait for web socket connections to be established
        TimeUnit.SECONDS.sleep(3);

        // --- submit move ---

        // when
        HttpPost submitMove = new HttpPost("https://127.0.0.1:" + httpsPort + "/submitMove");
        StringEntity entity = new StringEntity(objectMapper.writeValueAsString(new Move(uuidFactory.generateUUID(), player.getGameId(), Colour.YELLOW, 24, 32)));
        entity.setContentType("application/json;charset=UTF-8");
        submitMove.setEntity(entity);
        HttpResponse submitMoveResponse = httpClient.execute(submitMove);

        // then -- each web socket client has been updated
        assertEquals(HttpStatus.ACCEPTED.value(), submitMoveResponse.getStatusLine().getStatusCode());
        // wait for web socket message to be received
        TimeUnit.SECONDS.sleep(3);
        for (int i = 0; i < mockGameMoveListeners.length; i++) {
            System.out.println("i = " + i);
            GameMoveListener mockGameMoveListener = mockGameMoveListeners[i];
            verify(mockGameMoveListener).onMoveCompleted(any(Result.class));
        }
    }
}
