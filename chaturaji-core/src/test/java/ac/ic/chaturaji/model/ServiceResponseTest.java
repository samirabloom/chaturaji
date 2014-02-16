package ac.ic.chaturaji.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author samirarabbanian
 */
public class ServiceResponseTest {
    @Test
    public void shouldReturnFieldsSetInConstructor() {
        // when
        ServiceResponse serviceResponse = new ServiceResponse(ServiceResult.SUCCESS, "some message");

        // then
        assertEquals(ServiceResult.SUCCESS, serviceResponse.getResult());
        assertEquals("some message", serviceResponse.getMessage());
    }

    @Test
    public void shouldReturnFieldsSetBySetter() {
        // when
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setResult(ServiceResult.SUCCESS);
        serviceResponse.setMessage("some message");

        // then
        assertEquals(ServiceResult.SUCCESS, serviceResponse.getResult());
        assertEquals("some message", serviceResponse.getMessage());
    }
}
