package ac.ic.chaturaji.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author samirarabbanian
 */
public class UserTest {
    @Test
    public void shouldReturnFieldsSetInConstructor() {
        // when
        User user = new User("some id", "some nickname");

        // then
        assertEquals("some id", user.getId());
        assertEquals("some nickname", user.getNickname());
    }

    @Test
    public void shouldReturnFieldsSetBySetter() {
        // when
        User user = new User();
        user.setId("some id");
        user.setNickname("some nickname");

        // then
        assertEquals("some id", user.getId());
        assertEquals("some nickname", user.getNickname());
    }
}
