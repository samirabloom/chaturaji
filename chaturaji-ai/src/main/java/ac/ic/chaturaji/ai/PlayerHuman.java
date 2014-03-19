package ac.ic.chaturaji.ai;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayerHuman extends Player_AI {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /*------ Data Members -----*/

    MoveGenerator_AI ValidMoves;

    /*------ Methods -----*/

    // Constructor
    public PlayerHuman(int colour, int points, int[] kingsCaptured ){
        super(points, kingsCaptured);
        this.type = GameConstants.AI;
        this.SetColour(colour);
        ValidMoves = new MoveGenerator_AI();
    }

    public int GetPlayerType(){ return GameConstants.HUMAN; }

    // Functions

    // Ask for move from the human player:
    public Move_AI GetMove(Board_AI theBoard, int source, int dest){
        Move_AI move;
        // Generate all the possible moves for the player.
        ValidMoves.ComputeMoves(theBoard);

        logger.info("The Moves available to the current player are:");

        ValidMoves.Print();

        // Since the source/ destination square validation is done on the client side, we know that
        // it is a valid move that has been entered. Thus return the the move in the move list that goes from
        // the specified source to the specified destination (there can be only one!).
        move = ValidMoves.FindMove(source, dest);

        logger.info("The Moves chosen:");

        move.Print();

        setPoints(theBoard, move);

        return move;
    }
}