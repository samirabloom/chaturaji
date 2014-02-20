package ac.ic.chaturaji.ai;

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
        return game;
    }

    public Result submitMove(Game game, Move move) {
        Result result = null;

        //Set all the variables used in the AI module
        Board_AI board = new Board_AI(game.getBitboards());

        Player_AI players_AI[] = null;
        Player player[] = game.getPlayer();
        for( int i = 0; i < 4; i++){

            switch(player[i].getType()){
                case HUMAN:
                    players_AI[i] = new PlayerHuman_AI(player[i].getColour().ordinal(), player[i].getPoints(), player[i].getKingsCaptured());
                    break;
                case AI:
                    players_AI[i] = new PlayerAI_AI(player[i].getColour().ordinal(), player[i].getPoints(), player[i].getKingsCaptured());
                    break;
            }
        }

        Game_AI game_ai = new Game_AI(players_AI,board);

        // do move
        Colour currentPlayer = game.getCurrentPlayer();

        switch (player[currentPlayer.ordinal()].getType()){
            case HUMAN:
                PlayerHuman_AI humanPlayer = (PlayerHuman_AI) players_AI[currentPlayer.ordinal()];
                Move_AI theMove = new Move_AI(board.FindPieceColour(move.getSource(),move.getColour().ordinal()), move.getSource(),move.getDestination());

                //Check if the move submited is valid, if not return null
                if( !humanPlayer.CheckMove(board, theMove) ){
                    result.setType(ResultType.NOT_VALID);
                    return null;
                }
                else{
                    //Apply the move to the board, and construct the result object with the new state of the game
                    board.ApplyMove(theMove);
                    game.setBitboards(board.GetBitBoards());
                    game.getPlayer()[currentPlayer.ordinal()].setPoints(humanPlayer.GetPoints() );
                    game.setCurrentPlayer(Colour.values()[board.CurrentPlayer]);
                    result = new Result(GameStatus.IN_PLAY,game,move);
                    if( theMove.getTriumph())
                        result.setType(ResultType.BOAT_TRIUMPH);
                    else if( theMove.getCaptured() >= 0)
                        result.setType(ResultType.PIECE_TAKEN);
                    else
                        result.setType(ResultType.NONE_TAKEN);
                }
                break;
            case AI:
                //If it's AI turn, generate move
                PlayerAI_AI playerAI = (PlayerAI_AI) players_AI[currentPlayer.ordinal()];
                Move_AI moveAI = playerAI.GetMove(board);
                game.getPlayer()[currentPlayer.ordinal()].setPoints(playerAI.GetPoints());

                //Create Move and Game to return in a result object
                Move ResultMove = new Move();
                ResultMove.setSource(moveAI.getSource());
                ResultMove.setDestination(moveAI.getDest());
                ResultMove.setColour(currentPlayer);

                Game ResultGame = new Game();
                ResultGame.setCurrentPlayer(Colour.values()[board.GetCurrentPlayer()]);
                ResultGame.setBitboards(board.GetBitBoards());

                //Set the type of the move
                if( moveAI.getTriumph())
                    result.setType(ResultType.BOAT_TRIUMPH);
                else if( moveAI.getCaptured() >= 0)
                    result.setType(ResultType.PIECE_TAKEN);
                else
                    result.setType(ResultType.NONE_TAKEN);

                //Construct result object and return
                result = new Result(GameStatus.IN_PLAY,ResultGame,ResultMove);
                return result;
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
