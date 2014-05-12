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

    public AIMove GetMove(AIBoard board, int searchType, int difficulty) {
        //Generate all the moves
        AIMove move;

        if (searchType == 0) {
            move = new MTDF().Search(board, 15 - (15 * (1 / difficulty)));
        } else {
            move = new AlphaBeta().Search(board, board.getCurrentPlayer(), 6 - (6 * (1 / difficulty)));
        }

        // Set the points if a capture occurred.
        if (move != null) {
            setPoints(board, move);
            System.out.println("Player " + GameConstants.PlayerStrings[board.getCurrentPlayer()] + " has " + getPoints() + " points.");
        }

        //Return the move
        return move;
    }
}