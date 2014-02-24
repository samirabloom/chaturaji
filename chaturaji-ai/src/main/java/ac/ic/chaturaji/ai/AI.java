package ac.ic.chaturaji.ai;

import ac.ic.chaturaji.model.Colour;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.GameStatus;

import ac.ic.chaturaji.model.Colour;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.model.GameStatus;
import ac.ic.chaturaji.model.Move;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.Result;
import ac.ic.chaturaji.model.ResultType;

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

        //Create a new board and assign the new bitboards to the bitboards in game
        Board_AI board = new Board_AI();

        game.setBitboards(board.GetBitBoards());

        // Do we need to set the current player?
        // game.setCurrentPlayer(Colour.values()[board.GetCurrentPlayer()]);

        return game;
    }


    public Result submitMove(Game game, Move move) {
        Result result = null;

        int colour = game.getCurrentPlayer().ordinal();
        int source = move.getSource();
        int dest = move.getDestination();

        Player player = game.getPlayer()[colour];
        Move_AI theMove;
        PlayerHuman humanPlayer;
        PlayerComp playerAI;

        //Set all the variables used in the AI module
        Board_AI board = new Board_AI(game.getBitboards(), colour);

        switch (player.getType()) {
            case HUMAN: {
                humanPlayer = new PlayerHuman(colour, player.getPoints(), player.getKingsCaptured());

                theMove = humanPlayer.GetMove(board, source, dest);

                if(theMove == null){
                    result.setType(ResultType.NOT_VALID);
                    return null;
                }

                board.ApplyMove(theMove);

                game.setBitboards(board.GetBitBoards());
                game.getPlayer()[colour].setPoints(humanPlayer.GetPoints());
                game.setCurrentPlayer(Colour.values()[board.GetCurrentPlayer()]);

                result = new Result(GameStatus.IN_PLAY, game, move);

                if(theMove.getTriumph())
                    result.setType(ResultType.BOAT_TRIUMPH);
                else if( theMove.getCaptured() >= 0)
                    result.setType(ResultType.PIECE_TAKEN);
                else
                    result.setType(ResultType.NONE_TAKEN);

            } break;

            case AI: {
                playerAI = new PlayerComp(colour, player.getPoints(), player.getKingsCaptured());


                // If it's the AI's turn just generate a move:
                theMove = playerAI.GetMove(board);
                board.ApplyMove(theMove);

                game.getPlayer()[colour].setPoints(playerAI.GetPoints());

                //Create Move and Game to return in a result object
                Move ResultMove = new Move();
                ResultMove.setSource(theMove.getSource());
                ResultMove.setDestination(theMove.getDest());
                ResultMove.setColour(game.getCurrentPlayer());

                Game ResultGame = new Game();
                ResultGame.setCurrentPlayer(Colour.values()[board.GetCurrentPlayer()]);
                ResultGame.setBitboards(board.GetBitBoards());

                //Set the type of the move
                if(theMove.getTriumph())
                    result.setType(ResultType.BOAT_TRIUMPH);
                else if(theMove.getCaptured() >= 0)
                    result.setType(ResultType.PIECE_TAKEN);
                else
                    result.setType(ResultType.NONE_TAKEN);

                //Construct result object and return
                result = new Result(GameStatus.IN_PLAY, ResultGame, ResultMove);
            } break;
        }

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