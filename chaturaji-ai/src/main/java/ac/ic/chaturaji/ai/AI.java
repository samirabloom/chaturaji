package ac.ic.chaturaji.ai;

import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.Move;
import ac.ic.chaturaji.model.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
* @author samirarabbanian
*/
public class AI {

    Map<String, List<MoveListener>> moveListeners = new ConcurrentHashMap<>();

    public Game startGame(Game game) {

        // create board, etc

        return game;
    }

    public Result submitMove(Game game, Move move) {
        Result result = null;

        // do move

        synchronized (this) {
            List<MoveListener> moveListenersForGame = moveListeners.get(game.getId());
            if (moveListenersForGame != null) {
                for (MoveListener moveListener : moveListenersForGame) {
                    moveListener.pieceMoved(result);
                }
            }
        }
        return null;
    }

    public synchronized void registerListener(String gameId, MoveListener moveListener) {
        if (!moveListeners.containsKey(gameId)) {
            moveListeners.put(gameId, Collections.synchronizedList(new ArrayList<MoveListener>()));
        }
        moveListeners.get(gameId).add(moveListener);
    }

    public synchronized void unregisterListeners(String gameId) {
        moveListeners.remove(gameId);
    }
}
