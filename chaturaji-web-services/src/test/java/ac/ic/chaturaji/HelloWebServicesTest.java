package ac.ic.chaturaji;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author samirarabbanian
 */
public class HelloWebServicesTest {

    @Test
    public void shouldSayHello() {
        // given
        HelloWebServices helloWebServices = new HelloWebServices();

        // then
        assertEquals("web services", helloWebServices.sayHello());
    }
}
