package ac.ic.chaturaji.ai;

import java.util.Set;

/**
 * @author dg3213
 */
public class PlayerComp extends Player_AI {

    PlayerComp(int col, int points, Set<Integer> kingsCaptured) {
        super(points, kingsCaptured);
        this.type = GameConstants.HUMAN;
        colour = col;
    }

    public Move_AI GetMove(Board_AI board, int searchType) {
        //Generate all the moves
        Move_AI move;
        MTDF mtdf = new MTDF();

        if (searchType == 0)
            move = mtdf.Search(board);
        else
            move = new AlphaBeta().Search(board, board.GetCurrentPlayer(), 5);

        // Set the points if a capture occurred.
        if (move != null) {
            setPoints(board, move);
        }

        //Return the move
        return move;
    }
}