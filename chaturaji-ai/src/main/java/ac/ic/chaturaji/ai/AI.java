package ac.ic.chaturaji.ai;

import ac.ic.chaturaji.model.*;
import com.google.common.util.concurrent.SettableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author samirarabbanian
 */
@Component
public class AI {
    public static final int NUMBER_OF_MOVES_WITH_NO_PIECE_CAPTURED_FOR_STALEMATE = 25;
    public static final int AI_PLAYER_DELAY = 750;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, List<MoveListener>> moveListeners = new ConcurrentHashMap<>();

    public Game createGame(Game game) {
        game.setBitboards(new AIBoard().GetBitBoards());
        return game;
    }

    public Result submitMove(final Game game, final Move move) throws Exception {
        final SettableFuture<Result> futureResult = SettableFuture.create();

        final int colour = game.getCurrentPlayerColour().ordinal();
        final Player player = game.getPlayer(colour);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Result result = new Result(GameStatus.IN_PLAY, game, move);

                AIMove aiMove = null;
                AIPlayer aiPlayer = null;
                AIBoard board = new AIBoard(game.getBitboards(), colour);

                switch (player.getType()) {
                    case HUMAN: {
                        HumanPlayer humanPlayer = new HumanPlayer(colour, player.getPoints(), player.getKingsCaptured());

                        aiMove = humanPlayer.getMove(board, move.getSource(), move.getDestination());
                        aiPlayer = humanPlayer;

                        if (aiMove != null) {
                            board.ApplyMove(aiMove);
                            //board.Print();
                            //logger.debug(aiMove.Print());
                        } else {
                            result.setType(ResultType.NOT_VALID);
                        }
                    }
                    break;

                    case AI: {
                        ComputerPlayer computerPlayer = new ComputerPlayer(colour, player.getPoints(), player.getKingsCaptured());

                        aiMove = computerPlayer.GetMove(board, game.getAILevel());
                        aiPlayer = computerPlayer;

                        if (aiMove != null) {
                            board.ApplyMove(aiMove);
                            //board.Print();
                            //logger.debug(aiMove.Print());

                            move.setSource(aiMove.getSource());
                            move.setDestination(aiMove.getDestination());
                            move.setColour(game.getCurrentPlayerColour());
                        } else {
                            // No available move, so move on to the next player.
                            board.NextPlayer();
                        }
                    }
                    break;
                }

                game.getPlayer(colour).setPoints(aiPlayer.getPoints());
                game.setCurrentPlayerColour(Colour.values()[board.getCurrentPlayer()]);
                game.setBitboards(board.GetBitBoards());

                if (board.isGameOver() <= 1) {
                    result = new Result(GameStatus.GAME_OVER, game, move);
                }

                if (aiMove != null) {
                    if (aiMove.getTriumph()) {
                        result.setType(ResultType.BOAT_TRIUMPH);
                        game.resetStalemateCount();
                    } else if (aiMove.getType() > 0) {
                        result.setType(ResultType.PIECE_TAKEN);
                        game.resetStalemateCount();
                    } else {
                        result.setType(ResultType.NONE_TAKEN);
                        game.incrementStalemateCount();
                    }
                }

                if (result.getGameStatus() != GameStatus.GAME_OVER && game.getStalemateCount() >= NUMBER_OF_MOVES_WITH_NO_PIECE_CAPTURED_FOR_STALEMATE) {
                    result.setGameStatus(GameStatus.STALEMATE);
                }

                setAllPlayersCanMoveAnyPieceStatus(game);

                futureResult.set(result);
            }
        }).start();

        // DO NOT COMMENT THIS OUT THIS IS THE WAY THE SERVER INTEGRATES TO AI

        Result result;
        try {
            if (player.getType().equals(PlayerType.AI)) {
                // only wait if its an AI move
                result = futureResult.get(AI_PLAYER_DELAY, TimeUnit.MILLISECONDS);
            } else {
                result = futureResult.get();
            }
        } catch (Exception e) {
            // do nothing as expected exception for timeout
            result = futureResult.get();
        }
        synchronized (this) {
            if (!moveListeners.isEmpty()) {
                List<MoveListener> moveListenersForGame = moveListeners.get(game.getId());
                if (moveListenersForGame != null) {
                    for (MoveListener moveListener : new ArrayList<>(moveListenersForGame)) {
                        moveListener.pieceMoved(result);
                        // clean-up map of move listeners when the game is over
                        if (result.getGameStatus() == GameStatus.GAME_OVER || result.getGameStatus() == GameStatus.STALEMATE) {
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

    private void setAllPlayersCanMoveAnyPieceStatus(Game game) {
        for (Colour colour : Colour.values()) {
            Player player = game.getPlayer(colour.ordinal());
            if (player.getType() == PlayerType.HUMAN) {
                AIBoard board = new AIBoard(game.getBitboards(), colour.ordinal());
                HumanPlayer humanPlayer = new HumanPlayer(colour.ordinal(), player.getPoints(), player.getKingsCaptured());
                boolean canMovePiece = humanPlayer.getMoves(board).isEmpty();
                if (canMovePiece) {
                    logger.info("Updating player " + player + " to indicate no available moves");
                }
                player.setCanNotMoveAnyPiece(canMovePiece);
            }
        }
    }

    public synchronized void registerListener(String gameId, MoveListener moveListener) {
        if (!moveListeners.containsKey(gameId)) {
            moveListeners.put(gameId, Collections.synchronizedList(new ArrayList<MoveListener>()));
        }
        moveListeners.get(gameId).add(moveListener);
    }
}