package ac.ic.chaturaji.websockets;

import ac.ic.chaturaji.model.Result;

/**
 * @author samirarabbanian
 */
public interface GameMoveListener {

    public void onMoveCompleted(Result result);

}
