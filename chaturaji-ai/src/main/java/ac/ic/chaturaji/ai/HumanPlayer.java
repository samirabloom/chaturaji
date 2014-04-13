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

    AIMoveGenerator validMoves;

    /*------ Methods -----*/

    // Constructor

    public HumanPlayer(int colour, int points, Set<Integer> kingsCaptured) {
        super(points, kingsCaptured);
        this.type = GameConstants.AI;
        this.SetColour(colour);
        validMoves = new AIMoveGenerator();
    }

    // Functions

    // Ask for move from the human player:
    public AIMove getMove(AIBoard theBoard, int source, int dest) {
        AIMove move;
        // Generate all the possible moves for the player.
        validMoves.computeMoves(theBoard);

        validMoves.print();

        // Since the source/ destination square validation is done on the client side, we know that
        // it is a valid move that has been entered. Thus return the the move in the move list that goes from
        // the specified source to the specified destination (there can be only one!).
        move = validMoves.findMove(source, dest);

        if (move != null) {
            logger.debug("The move chosen is: " + move.Print());
            setPoints(theBoard, move);
        }

        return move;
    }

    public ArrayList<AIMove> getMoves(AIBoard theBoard) {
        return validMoves.computeMoves(theBoard);
    }
}