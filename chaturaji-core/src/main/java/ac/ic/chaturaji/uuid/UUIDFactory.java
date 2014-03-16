package ac.ic.chaturaji.uuid;

import java.util.UUID;

/**
 * @author samirarabbanian
 */
public class UUIDFactory {

    public String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
