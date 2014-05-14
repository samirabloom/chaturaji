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

    public AIMove GetMove(AIBoard board, int difficulty) {
        //Generate all the moves
        AIMove move;

        move = new MTDF().Search(board, (int) (1.5 * difficulty) - 1);

        // Set the points if a capture occurred.
        if (move != null) {
            setPoints(board, move);
            //System.out.println("Player " + GameConstants.PlayerStrings[board.getCurrentPlayer()] + " has " + getPoints() + " points.");
        }

        //Return the move
        return move;
    }
}