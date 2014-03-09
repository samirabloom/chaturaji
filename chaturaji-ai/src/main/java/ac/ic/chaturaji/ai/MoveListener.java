package ac.ic.chaturaji.ai;

import ac.ic.chaturaji.model.Result;
import org.codehaus.jackson.JsonGenerationException;

import java.io.IOException;

/**
 * @author samirarabbanian
 */
public interface MoveListener {

    public void pieceMoved(Result result);
}
