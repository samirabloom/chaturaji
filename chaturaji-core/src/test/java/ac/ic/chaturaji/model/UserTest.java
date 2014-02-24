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
        User user = new User("some id", "some email", "some password", "some nickname");

        // then
        assertEquals("some id", user.getId());
        assertEquals("some email", user.getEmail());
        assertEquals("some password", user.getPassword());
        assertEquals("some nickname", user.getNickname());
    }

    @Test
    public void shouldReturnFieldsSetBySetter() {
        // when
        User user = new User();
        user.setId("some id");
        user.setEmail("some email");
        user.setNickname("some nickname");

        // then
        assertEquals("some id", user.getId());
        assertEquals("some email", user.getEmail());
        assertEquals("some nickname", user.getNickname());
    }
}
