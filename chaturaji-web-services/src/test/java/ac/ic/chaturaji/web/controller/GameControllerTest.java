package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.ai.AI;
import ac.ic.chaturaji.dao.GameDAO;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.User;
import ac.ic.chaturaji.security.SpringSecurityUserContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
    private SpringSecurityUserContext springSecurityUserContext;
    @InjectMocks
    private GameController gameController;

    @Before
    public void setupMocks() {
        gameController = new GameController();

        initMocks(this);
    }

    @Test
    public void shouldGetGamesFromDAOAndCreateJSON() throws IOException {
        // given
        List<Game> games = Arrays.asList(new Game("a", new Player(new User())));
        when(gameDAO.getAll()).thenReturn(games);
        when(objectMapper.writerWithDefaultPrettyPrinter()).thenReturn(objectWriter);
        when(objectWriter.writeValueAsString(games)).thenReturn("json");

        // when
        String result = gameController.getGameList();

        // then
        assertEquals("json", result);

        verify(gameDAO).getAll();
        verify(objectWriter).writeValueAsString(Arrays.asList(new Game("a", new Player(new User()))));
    }

    @Test
    public void shouldSaveGameToDAOSuccessfully() throws IOException {
        // when
        ResponseEntity<String> result = gameController.createGame(0);

        // then
        assertEquals("", result.getBody());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());

        verify(gameDAO).save(any(Game.class));
    }

    @Test
    public void shouldValidateNumberOfAIPlayerNotTooLarge() throws IOException {
        // when
        ResponseEntity<String> result = gameController.createGame(5);

        // then
        assertEquals("Invalid numberOfAIPlayers: 5 is not between 0 and 3 inclusive", result.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());

        verifyNoMoreInteractions(gameDAO);
    }

    @Test
    public void shouldValidateNumberOfAIPlayerNotTooSmall() throws IOException {
        // when
        ResponseEntity<String> result = gameController.createGame(-1);

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
        ResponseEntity<String> result = gameController.createGame(0);

        // then
        assertEquals("test exception", result.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());

        verify(gameDAO).save(any(Game.class));
    }


}
