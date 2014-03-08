package ac.ic.chaturaji.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author samirarabbanian
 */
public class PlayerTest {
    @Test
    public void shouldReturnFieldsSetInConstructor() {
        // when
        User user = new User();
        Player player = new Player(user);

        // then
        assertEquals(user, player.getUser());
    }

    @Test
    public void shouldReturnFieldsSetBySetter() {
        // when
        User user = new User();
        Player player = new Player();
        player.setId("some id");
        player.setUser(user);

        // then
        assertEquals("some id", player.getId());
        assertEquals(user, player.getUser());
    }
}
