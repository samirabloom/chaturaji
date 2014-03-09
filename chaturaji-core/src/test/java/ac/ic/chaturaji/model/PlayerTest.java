package ac.ic.chaturaji.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

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
        player.setType(PlayerType.HUMAN);
        player.setColour(Colour.RED);
        player.setPoints(10);
        int[] kingsCaptured = {1, 2, 3};
        player.setKingsCaptured(kingsCaptured);
        player.setUser(user);

        // then
        assertEquals("some id", player.getId());
        assertSame(user, player.getUser());
        assertEquals(PlayerType.HUMAN, player.getType());
        assertEquals(Colour.RED, player.getColour());
        assertEquals(10, player.getPoints());
        assertSame(kingsCaptured, player.getKingsCaptured());
    }
}
