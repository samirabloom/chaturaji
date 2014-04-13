package ac.ic.chaturaji.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author dg3213
 */
public class HumanPlayer extends AIPlayer {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /*------ Data Members -----*/

    MoveGenerator_AI ValidMoves;

    /*------ Methods -----*/

    // Constructor

    public HumanPlayer(int colour, int points, Set<Integer> kingsCaptured) {
        super(points, kingsCaptured);
        this.type = GameConstants.AI;
        this.SetColour(colour);
        ValidMoves = new MoveGenerator_AI();
    }

    // Functions

    // Ask for move from the human player:
    public AIMove GetMove(AIBoard theBoard, int source, int dest) {
        AIMove move;
        // Generate all the possible moves for the player.
        ValidMoves.ComputeMoves(theBoard);

        ValidMoves.Print();

        // Since the source/ destination square validation is done on the client side, we know that
        // it is a valid move that has been entered. Thus return the the move in the move list that goes from
        // the specified source to the specified destination (there can be only one!).
        move = ValidMoves.FindMove(source, dest);

        if (move != null) {
            logger.debug("The move chosen is: " + move.Print());
        }

        setPoints(theBoard, move);

        return move;
    }

    public ArrayList<AIMove> getMoves(AIBoard theBoard) {
        return ValidMoves.ComputeMoves(theBoard);
    }
}