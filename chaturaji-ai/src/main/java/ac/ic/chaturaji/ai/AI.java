package ac.ic.chaturaji.ai;

import ac.ic.chaturaji.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author samirarabbanian
 */
@Component
public class AI {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, List<MoveListener>> moveListeners = new ConcurrentHashMap<>();

    public Game createGame(Game game) {

        //Create a new board and set up the bitboards within the Game class:
        Board_AI board = new Board_AI();
        board.Print();

        game.setBitboards(board.GetBitBoards());

        return game;
    }


    public Result submitMove(Game game, Move move) {
        Result result = null;

        int colour = game.getCurrentPlayerColour().ordinal();

        Player player = game.getPlayer(colour);
        Move_AI theMove;
        PlayerHuman humanPlayer;
        PlayerComp playerAI;

        //Set all the variables used in the AI module
        Board_AI board = new Board_AI(game.getBitboards(), colour);

        switch (player.getType()) {
            case HUMAN: {
                humanPlayer = new PlayerHuman(colour, player.getPoints(), player.getKingsCaptured());

                theMove = humanPlayer.GetMove(board, move.getSource(), move.getDestination());

                if (theMove == null) {
                    result = new Result(GameStatus.IN_PLAY, game, move);
                    result.setType(ResultType.NOT_VALID);
                    break;
                }

                board.ApplyMove(theMove);
                //board.Print();
                //theMove.Print();

                game.setBitboards(board.GetBitBoards());
                game.getPlayer(colour).setPoints(humanPlayer.GetPoints());
                game.setCurrentPlayerColour(Colour.values()[board.GetCurrentPlayer()]);

                if (board.isGameOver() == 0 || game.getStalemateCount() == 10)
                    result = new Result(GameStatus.GAME_OVER, game, move);
                else
                    result = new Result(GameStatus.IN_PLAY, game, move);

                if (theMove.getTriumph()) {
                    result.setType(ResultType.BOAT_TRIUMPH);
                    game.resetStalemateCount();
                } else if (theMove.getType() > 0) {
                    result.setType(ResultType.PIECE_TAKEN);
                    game.resetStalemateCount();
                } else {
                    result.setType(ResultType.NONE_TAKEN);
                    game.incrementStalemateCount();
                }

                // Finally we check if a stalemate has occured:
                if (result.getGameStatus() != GameStatus.GAME_OVER)
                    if (game.getStalemateCount() >= 10)
                        result.setGameStatus(GameStatus.STALEMATE);

                boolean canMovePiece = humanPlayer.getMoves(board).isEmpty();
                if (canMovePiece) {
                    logger.info("Updating player " + player + " to indicate no available moves");
                }
                player.setCanNotMoveAnyPiece(canMovePiece);
            }
            break;

            case AI: {
                playerAI = new PlayerComp(colour, player.getPoints(), player.getKingsCaptured());

                Random randomGenerator = new Random();
                int random = randomGenerator.nextInt();

                // If it's the AI's turn just generate a move:
                theMove = playerAI.GetMove(board, (random % 2));

                if (theMove != null) {
                    board.ApplyMove(theMove);
                    board.Print();
                    System.out.println(theMove.Print());

                    //Create Move and Game to return in a result object
                    //Move ResultMove = new Move();
                    move.setSource(theMove.getSource());
                    move.setDestination(theMove.getDest());
                    move.setColour(game.getCurrentPlayerColour());
                } else {
                    // If the AI returns a null move, it is because the current player cannot make one - either because the player's
                    // pieces are blocked or because they have been eliminated. If so, move on to the next player.
                    board.NextPlayer();
                }
                //Game ResultGame = new Game();

                game.getPlayer(colour).setPoints(playerAI.GetPoints());
                game.setCurrentPlayerColour(Colour.values()[board.GetCurrentPlayer()]);
                game.setBitboards(board.GetBitBoards());

                if (board.isGameOver() <= 1)
                    result = new Result(GameStatus.GAME_OVER, game, move);
                else
                    result = new Result(GameStatus.IN_PLAY, game, move);

                if (theMove != null) {
                    //Set the type of the move
                    if (theMove.getTriumph()) {
                        result.setType(ResultType.BOAT_TRIUMPH);
                        game.resetStalemateCount();
                    } else if (theMove.getType() > 0) {
                        result.setType(ResultType.PIECE_TAKEN);
                        game.resetStalemateCount();
                    } else {
                        result.setType(ResultType.NONE_TAKEN);
                        game.incrementStalemateCount();
                    }
                }

                // Check for stalemate:
                if (result.getGameStatus() != GameStatus.GAME_OVER)
                    if (game.getStalemateCount() >= 10)
                        result.setGameStatus(GameStatus.STALEMATE);
            }
            break;
        }
        // DO NOT COMMENT THIS OUT THIS IS THE WAY THE SERVER INTEGRATES TO AI

        synchronized (this) {
            if (!moveListeners.isEmpty()) {
                List<MoveListener> moveListenersForGame = moveListeners.get(game.getId());
                if (moveListenersForGame != null) {
                    for (MoveListener moveListener : new ArrayList<>(moveListenersForGame)) {
                        moveListener.pieceMoved(result);
                        // clean-up map of move listeners when the game is over
                        if (result.getGameStatus() == GameStatus.GAME_OVER) {
                            if (moveListeners.get(game.getId()) != null) {
                                // remove this listener from list for game
                                moveListeners.get(game.getId()).remove(moveListener);
                                if (moveListeners.get(game.getId()).size() == 0) {
                                    // if no more listeners for this game remove list of listeners for this game
                                    moveListeners.remove(game.getId());
                                }
                            }
                        }
                    }
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