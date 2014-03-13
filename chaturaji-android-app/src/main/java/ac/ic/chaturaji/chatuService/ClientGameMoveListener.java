package ac.ic.chaturaji.chatuService;

import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Move;
import ac.ic.chaturaji.model.Result;
import ac.ic.chaturaji.websockets.GameMoveListener;
import android.app.Activity;

/**
 * Created by Haider on 12/03/14.
 */
public class ClientGameMoveListener implements GameMoveListener {

    private Game game = null;
    private Move move = null;
    private Activity activity;
    private OnMoveCompleteListener mCallback;

    public ClientGameMoveListener(Activity activity){

        this.activity = activity;

        mCallback = (OnMoveCompleteListener) activity;

    }

    public void onMoveCompleted(Result result) {

        System.out.println("Got the move: ");
        System.out.println(result.getMove());


        mCallback.updateGame(result);
    }

}
