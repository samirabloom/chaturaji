package ac.ic.chaturaji;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author samirarabbanian
 */
public class HelloWorldTest {

    @Test
    public void shouldSayHello() {
        // given
        HelloWorld helloWorld = new HelloWorld();

        // then
        assertEquals("hello", helloWorld.sayHello());
    }
}
