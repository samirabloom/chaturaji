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

	AIBoard board = new AIBoard();

	game.setBitboard(board.ReturnBitBoards());
	game.setCurrentPlayer(board.GetCurrentPlayer());

        return game;
    }

    public Result submitMove(Game game, Move move) {
        Result result = null;
	PlayerHuman human;
	PlayerComp ai;
	AIMove Mov;
	int piece;
	int colour = move.getColour();

	AIBoard board = new AIBoard(game.getBitBoards(), colour);

	if (game.getPlayer() == PlayerType.HUMAN) {

	    // First find the piece that the move given specifies:
	    piece = board.FindPieceColour(move.getSource(), colour);
	    
	    // Create an AIMove to check if the move given is valid.

	    Mov = new AIMove(piece, move.getSource(), move.getDestination());
	    human = new PlayerHuman(game.setCurrentPlayer, Mov);
	    human.GetMove(game.getBitBoards());

	    // Suppose the move is valid, then update the board:
	    board.ApplyMove(Mov);
      
	    // ApplyMove changes the bitboards. Return them to the game class:
	    game.setBitBoard(board.ReturnBitBoards());

	    // Set the current player in the game as the next colour to move:
	    game.setCurrentPlayer(board.GetCurrentPlayer);

	    // Set the values in the result class. Not too sure about this,
	    // will have to check it properly!
	    // Since it is a human player I assume we will always return IN_PLAY,
	    // as they can decide whether they wish to resign on the client side?

	    result.setGameStatus(GameStatus.IN_PLAY);
	    result.setType(Mov.getType());
	    result.setGame(game);
	    result.setMove(move);
	}
	else {
	    
	    // Easier on this side, just create an PlayerComp class and generate the
	    // best move.
	    ai = new PlayerComp(colour);
	    Mov = (AIMove)ai.GetMove(board);
	    
	    // I presume 'move' will be empty, so set its members?
	    move.setSource(Mov.getSource());
	    move.setDestination(Mov.getDest());
	    move.setColour(board.GetCurrentPlayer());

	    // Apply the move and change the bit boards:
	    board.ApplyMove(Mov);
      
	    game.setBitBoard(board.ReturnBitBoards());
	    game.setCurrentPlayer(board.GetCurrentPlayer);
	    
	    // Check if the AI chooses to resign:
	    if (Mov.getType() == GameConstants.RESIGN) 
		result.setGameStatus(GameStatus.GAME_OVER);
	    else
		result.setgameStatus(GameStatus.IN_PLAY);
	}
	
	// We may need to change some bits here I think, as on the AI side
	// we have a choice between normal move/ capture/ resign as move types
	// (and boat triumph is done separately). This would be straighforward though.

	result.setType(Mov.getType());
	result.setGame(game);
	result.setMove(move);  

        synchronized (this) {
            List<MoveListener> moveListenersForGame = moveListeners.get(game.getId());
            if (moveListenersForGame != null) {
                for (MoveListener moveListener : moveListenersForGame) {
                    moveListener.pieceMoved(result);
                }
            }
        }

        return result;
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
