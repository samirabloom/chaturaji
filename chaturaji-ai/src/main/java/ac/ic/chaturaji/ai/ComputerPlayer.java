package ac.ic.chaturaji.ai;

import java.util.Set;

/**
 * @author dg3213
 */
public class ComputerPlayer extends AIPlayer {

    ComputerPlayer(int col, int points, Set<Integer> kingsCaptured) {
        super(points, kingsCaptured);
        this.type = GameConstants.HUMAN;
        colour = col;
    }

    public AIMove GetMove(AIBoard board, int searchType) {
        //Generate all the moves
        AIMove move;

        if (searchType == 0) {
            move = new MTDF().Search(board);
        } else {
            move = new AlphaBeta().Search(board, board.getCurrentPlayer(), 6);
        }

        // Set the points if a capture occurred.
        if (move != null) {
            setPoints(board, move);
        }

        //Return the move
        return move;
    }
}