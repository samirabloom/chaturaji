package ac.ic.chaturaji;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author samirarabbanian
 */
public class HelloAndroidTest {

    @Test
    public void shouldSayHello() {
        // given
        HelloAndroid helloAndroid = new HelloAndroid();

        // then
        assertEquals("android", helloAndroid.sayHello());
    }
}
