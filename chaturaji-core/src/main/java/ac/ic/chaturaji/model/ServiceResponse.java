package ac.ic.chaturaji.model;

/**
 * @author samirarabbanian
 */
public class ServiceResponse extends EqualsHashCodeToString {
    private ServiceResult result;
    private String message;

    // Dummy constructor needed to map JSON string back to Java object
    public ServiceResponse() {
    }

    public ServiceResponse(ServiceResult result, String message) {
        this.result = result;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ServiceResult getResult() {
        return result;
    }

    public void setResult(ServiceResult result) {
        this.result = result;
    }

}
