package ac.ic.chaturaji.mockmvc;

import ac.ic.chaturaji.config.RootConfiguration;
import ac.ic.chaturaji.dao.GameDAO;
import ac.ic.chaturaji.dao.PlayerDAO;
import ac.ic.chaturaji.model.*;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import ac.ic.chaturaji.security.SpringSecurityUserContext;
import ac.ic.chaturaji.uuid.UUIDFactory;
import ac.ic.chaturaji.web.config.WebMvcConfiguration;
import ac.ic.chaturaji.web.controller.InMemoryGamesContextListener;
import ac.ic.chaturaji.web.websockets.WebSocketServletContextListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * @author samirarabbanian
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({
        @ContextConfiguration(
                classes = {
                        RootConfiguration.class
                }
        ),
        @ContextConfiguration(
                classes = {
                        WebMvcConfiguration.class,
                        GameControllerMockMVCIntegrationTest.MockConfiguration.class
                }
        )
})
public class GameControllerMockMVCIntegrationTest {

    @Resource
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Resource
    private GameDAO gameDAO;
    @Resource
    private PlayerDAO playerDAO;
    @Resource
    private UUIDFactory uuidFactory;
    @Resource
    private SpringSecurityUserContext springSecurityUserContext;

    @Resource
    private ServletContext servletContext;

    @Before
    public void setupFixture() {
        mockMvc = webAppContextSetup(webApplicationContext)
                .alwaysDo(print())
                .build();
        servletContext.removeAttribute(InMemoryGamesContextListener.IN_MEMORY_GAMES_ATTRIBUTE_NAME);
        servletContext.removeAttribute(WebSocketServletContextListener.WEB_SOCKET_CLIENT_ATTRIBUTE_NAME);
        servletContext.removeAttribute(WebSocketServletContextListener.WEB_SOCKET_CLIENT_REGISTRATION_LISTENERS_ATTRIBUTE_NAME);
    }

    @Test
    public void shouldLoadListOfGames() throws Exception {

        // given
        String[] gameIds = new String[]{
                uuidFactory.generateUUID(),
                uuidFactory.generateUUID(),
                uuidFactory.generateUUID(),
                uuidFactory.generateUUID()
        };
        when(gameDAO.getAll()).thenReturn(Arrays.asList(
                new Game("1", new Player(gameIds[0], new User(), Colour.YELLOW, PlayerType.HUMAN)),
                new Game("2", new Player(gameIds[1], new User(), Colour.YELLOW, PlayerType.HUMAN)),
                new Game("3", new Player(gameIds[2], new User(), Colour.YELLOW, PlayerType.HUMAN)),
                new Game("4", new Player(gameIds[3], new User(), Colour.YELLOW, PlayerType.HUMAN))
        ));

        // when
        mockMvc.perform(
                get("/games")
                        .accept(MediaType.APPLICATION_JSON)
        )
                // then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=" + StandardCharsets.UTF_8))
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    public void shouldCreateGame() throws Exception {
        // when
        MvcResult result = mockMvc.perform(
                post("/createGame")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("numberOfAIPlayers", "0")
        )
                // then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=" + StandardCharsets.UTF_8))
                .andReturn();

        Player player = new ObjectMapperFactory().createObjectMapper().readValue(result.getResponse().getContentAsString(), Player.class);
        assertEquals(Colour.YELLOW, player.getColour());
        assertEquals(PlayerType.HUMAN, player.getType());

        verify(gameDAO).save(new Game(uuidFactory.generateUUID(), new Player(uuidFactory.generateUUID(), new User(), Colour.YELLOW, PlayerType.HUMAN)));
    }

    @Test
    public void shouldValidateNumberOfPlayersNotTooSmallWhenCreatingGame() throws Exception {
        // when
        MvcResult result = mockMvc.perform(
                post("/createGame")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("numberOfAIPlayers", "-1")
        )
                // then
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("\"Invalid numberOfAIPlayers: -1 is not between 0 and 3 inclusive\"", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldValidateNumberOfPlayersNotTooLargeWhenCreatingGame() throws Exception {
        // when
        MvcResult result = mockMvc.perform(
                post("/createGame")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("numberOfAIPlayers", "5")
        )
                // then
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("\"Invalid numberOfAIPlayers: 5 is not between 0 and 3 inclusive\"", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldJoinGame() throws Exception {
        // given
        final String gameId = "existing_game";
        servletContext.setAttribute(InMemoryGamesContextListener.IN_MEMORY_GAMES_ATTRIBUTE_NAME, new HashMap<String, Game>() {{
            put(gameId, new Game(gameId, new Player(uuidFactory.generateUUID(), new User(), Colour.YELLOW, PlayerType.HUMAN)));
        }});
        springSecurityUserContext.setCurrentUser(new User("some id", "some email", "some password", "some nickname"));

        // when
        MvcResult result = mockMvc.perform(
                post("/joinGame")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("gameId", gameId)
        )
                // then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=" + StandardCharsets.UTF_8))
                .andReturn();

        Player player = new ObjectMapperFactory().createObjectMapper().readValue(result.getResponse().getContentAsString(), Player.class);
        assertEquals(Colour.BLUE, player.getColour());
        assertEquals(PlayerType.HUMAN, player.getType());

        Player newPlayer = new Player(uuidFactory.generateUUID(), springSecurityUserContext.getCurrentUser(), Colour.BLUE, PlayerType.HUMAN);
        newPlayer.setGameId(gameId);
        verify(playerDAO).save(gameId, newPlayer);
    }

    @Test
    public void shouldValidateGameExistsWhenJoiningGame() throws Exception {
        // when
        MvcResult result = mockMvc.perform(
                post("/joinGame")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("gameId", "not_existing_game")
        )
                // then
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("\"No game found with gameId: not_existing_game\"", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldValidateGameNotFullWhenJoiningGame() throws Exception {
        // given
        final String gameId = "full_game";
        servletContext.setAttribute(InMemoryGamesContextListener.IN_MEMORY_GAMES_ATTRIBUTE_NAME, new HashMap<String, Game>() {{
            put(gameId,
                    new Game(gameId, new Player(uuidFactory.generateUUID(), new User(), Colour.YELLOW, PlayerType.HUMAN))
                            .addPlayer(new Player(uuidFactory.generateUUID(), new User(), Colour.BLUE, PlayerType.HUMAN))
                            .addPlayer(new Player(uuidFactory.generateUUID(), new User(), Colour.RED, PlayerType.HUMAN))
                            .addPlayer(new Player(uuidFactory.generateUUID(), new User(), Colour.GREEN, PlayerType.HUMAN))
            );
        }});

        // when
        MvcResult result = mockMvc.perform(
                post("/joinGame")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("gameId", gameId)
        )
                // then
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("\"Game already has four players\"", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldValidateNotAlreadyJoinedGame() throws Exception {
        // given
        final String gameId = "already_joined_game";
        springSecurityUserContext.setCurrentUser(new User("some id", "some email", "some password", "some nickname"));
        servletContext.setAttribute(InMemoryGamesContextListener.IN_MEMORY_GAMES_ATTRIBUTE_NAME, new HashMap<String, Game>() {{
            put(gameId, new Game(gameId, new Player(uuidFactory.generateUUID(), springSecurityUserContext.getCurrentUser(), Colour.YELLOW, PlayerType.HUMAN))
            );
        }});

        // when
        MvcResult result = mockMvc.perform(
                post("/joinGame")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("gameId", gameId)
        )
                // then
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("\"You have already joined this game\"", result.getResponse().getContentAsString());
    }

    @Configuration
    static class MockConfiguration {

        @Bean
        public UUIDFactory uuidFactory() {
            UUIDFactory mockUUIDFactory = mock(UUIDFactory.class);
            when(mockUUIDFactory.generateUUID()).thenReturn("random_uuid");
            return mockUUIDFactory;
        }

        @Bean
        public SpringSecurityUserContext springSecurityUserContext() {
            return new SpringSecurityUserContext() {
                private User user;

                @Override
                public User getCurrentUser() {
                    return this.user;
                }

                @Override
                public void setCurrentUser(User user) {
                    this.user = user;
                }
            };
        }

        @Bean
        public GameDAO gameDAO() {
            return mock(GameDAO.class);
        }

        @Bean
        public PlayerDAO playerDAO() {
            return mock(PlayerDAO.class);
        }
    }
}
