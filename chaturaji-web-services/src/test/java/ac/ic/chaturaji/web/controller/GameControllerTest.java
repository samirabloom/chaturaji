package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.ai.AI;
import ac.ic.chaturaji.dao.GameDAO;
import ac.ic.chaturaji.model.*;
import ac.ic.chaturaji.security.SpringSecurityUserContext;
import ac.ic.chaturaji.uuid.UUIDFactory;
import ac.ic.chaturaji.web.websockets.WebSocketServletContextListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.channels.Channel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author samirarabbanian
 */
public class GameControllerTest {

    @Mock
    private GameDAO gameDAO;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ObjectWriter objectWriter;
    @Mock
    private AI ai;
    @Mock
    private ServletContext servletContext;
    @Mock
    private SpringSecurityUserContext springSecurityUserContext;
    @Mock
    private UUIDFactory uuidFactory;
    @InjectMocks
    private GameController gameController;
    private HashMap<String,Game> games;

    @Before
    public void setupMocks() {
        gameController = new GameController();

        initMocks(this);

        when(uuidFactory.generateUUID()).thenReturn("randomId");
        games = new HashMap<String, Game>();
        when(servletContext.getAttribute(InMemoryGamesContextListener.IN_MEMORY_GAMES_ATTRIBUTE_NAME)).thenReturn(games);
    }

    @Test
    public void shouldGetGamesFromDAOAndCreateJSON() throws IOException {
        // given
        List<Game> games = Arrays.asList(new Game("a", new Player("player id", new User(), Colour.YELLOW, PlayerType.HUMAN)));
        when(gameDAO.getAll()).thenReturn(games);

        // when
        List<Game> result = gameController.getGameList();

        // then
        verify(gameDAO).getAll();
        assertEquals(games, result);
    }

    @Test
    public void shouldSaveGameToDAOSuccessfully() throws IOException {
        // when
        when(servletContext.getAttribute(WebSocketServletContextListener.WEB_SOCKET_CLIENT_ATTRIBUTE_NAME)).thenReturn(new HashMap<String, Channel>());
        ResponseEntity result = gameController.createGame(0);

        // then
        // check valid id is returned
        assertThat(result.getBody(), instanceOf(Player.class));
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        verify(gameDAO).save(any(Game.class));
        assertTrue(games.containsKey(uuidFactory.generateUUID()));
    }

    @Test
    public void shouldValidateNumberOfAIPlayerNotTooLarge() throws IOException {
        // when
        ResponseEntity result = gameController.createGame(5);

        // then
        assertEquals("Invalid numberOfAIPlayers: 5 is not between 0 and 3 inclusive", result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());

        verifyNoMoreInteractions(gameDAO);
    }

    @Test
    public void shouldValidateNumberOfAIPlayerNotTooSmall() throws IOException {
        // when
        ResponseEntity result = gameController.createGame(-1);

        // then
        assertEquals("Invalid numberOfAIPlayers: -1 is not between 0 and 3 inclusive", result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());

        verifyNoMoreInteractions(gameDAO);
    }

    @Test
    public void shouldHandleDAOException() throws IOException {
        // given
        doThrow(new RuntimeException("test exception")).when(gameDAO).save(any(Game.class));

        // when
        ResponseEntity result = gameController.createGame(0);

        // then
        assertEquals("test exception", result.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());

        verify(gameDAO).save(any(Game.class));
    }


}
