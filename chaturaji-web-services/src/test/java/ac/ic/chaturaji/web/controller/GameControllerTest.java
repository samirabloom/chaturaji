package ac.ic.chaturaji.web.controller;

import ac.ic.chaturaji.dao.GameDAO;
import ac.ic.chaturaji.model.Game;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author samirarabbanian
 */
public class GameControllerTest {

    @Mock
    private GameDAO gameDAO;
    @Mock
    private ObjectMapper objectMapper;
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
        List<Game> games = Arrays.asList(new Game("a"));
        when(gameDAO.getAll()).thenReturn(games);
        when(objectMapper.writeValueAsString(games)).thenReturn("json");

        // when
        String result = gameController.getGameList();

        // then
        assertEquals("json", result);

        verify(gameDAO).getAll();
        verify(objectMapper).writeValueAsString(Arrays.asList(new Game("a")));
    }


}
