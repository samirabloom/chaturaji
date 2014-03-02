package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.config.RootConfiguration;
import ac.ic.chaturaji.dao.GameDAO;
import ac.ic.chaturaji.web.config.WebMvcConfiguration;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
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
                        GameControllerMockMVCIntegrationTest.MockDaoConfiguration.class
                }
        )
})
public class GameControllerMockMVCIntegrationTest {

    @Resource
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @Resource
    private GameDAO gameDAO;

    @Before
    public void setupFixture() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

   /* @Test
    public void shouldLoadListOfGames() throws Exception {
        // given
        when(gameDAO.getAll()).thenReturn(Arrays.asList(
                new Game("1", new Player(new User())),
                new Game("2", new Player(new User())),
                new Game("3", new Player(new User())),
                new Game("4", new Player(new User()))
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
                post("/game")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("numberOfAIPlayers", "0")
        )
                // then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON + ";charset=" + StandardCharsets.UTF_8))
                .andReturn();

        assertEquals("", result.getResponse().getContentAsString());
    }
*/
    @Test
    public void shouldValidateNumberOfPlayersNotTooSmall() throws Exception {
        // when
        MvcResult result = mockMvc.perform(
                post("/game")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("numberOfAIPlayers", "-1")
        )
                // then
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("Invalid numberOfAIPlayers: -1 is not between 0 and 3 inclusive", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldValidateNumberOfPlayersNotTooLarge() throws Exception {
        // when
        MvcResult result = mockMvc.perform(
                post("/game")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("numberOfAIPlayers", "5")
        )
                // then
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("Invalid numberOfAIPlayers: 5 is not between 0 and 3 inclusive", result.getResponse().getContentAsString());
    }

    @Configuration
    static class MockDaoConfiguration {

        @Bean
        public GameDAO gameDAO() {
            return mock(GameDAO.class);
        }
    }
}
