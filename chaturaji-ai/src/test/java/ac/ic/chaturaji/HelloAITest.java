package ac.ic.chaturaji;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author samirarabbanian
 */
public class HelloAITest {

    @Test
    public void shouldSayAI() {
        // given
        HelloAI helloAI = new HelloAI();

        // then
        assertEquals("ai", helloAI.sayHello());
    }
}
