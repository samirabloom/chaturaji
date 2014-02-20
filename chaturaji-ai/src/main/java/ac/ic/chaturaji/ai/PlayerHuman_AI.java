package ac.ic.chaturaji.ai;
//import Project.Chaturaji.*;
public class PlayerHuman_AI extends Player_AI {

    /*------ Data Members -----*/

    MoveGenerator_AI ValidMoves;


    /*------ Methods -----*/

    // Constructor
    public PlayerHuman_AI( int which, int points, int[] kingsCaptured ){
        super(points, kingsCaptured);
	    this.SetColour( which );
	    ValidMoves = new MoveGenerator_AI();

    }

    // Functions

    // This function should be to validate a move
    public boolean CheckMove( Board_AI theBoard, Move_AI theMove ){

        int colour = theBoard.GetCurrentPlayer();

        ValidMoves.DetermineMove(theBoard, colour ,theMove );
        CheckPawnPromotion(theMove, theBoard, colour);

        // Check if move is legal by generating all possible moves available to the current player with respect
        // to the current board position.
        ValidMoves.validateMove(theBoard, colour,theBoard.FindPieceColour(theMove.getSource(),colour)-colour);
        if (!ValidMoves.IsMove(theMove))
        {
            return false;
        }
        setPoints(theBoard, theMove);
        return true;
    }

}