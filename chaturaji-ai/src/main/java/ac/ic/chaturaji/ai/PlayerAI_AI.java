package ac.ic.chaturaji.ai;
/**
 * Created by Asus on 17/02/14.
 */
public class PlayerAI_AI extends Player_AI{
    int colour;
    MoveGenerator_AI ValidMoves;

    PlayerAI_AI(int col,int points, int[] kingsCaptured){
        super(points, kingsCaptured);
        colour = col;
    }

    public Move_AI GetMove(Board_AI theBoard){
        //Generate all the moves
        ValidMoves.ComputeMoves(theBoard);
        //Select the move
        Move_AI moveAI = ValidMoves.Moves.get(0);
        //Set the kind of move: CAPTURE, BOAT_TRIUMPH, etc
        ValidMoves.DetermineMove(theBoard,colour,moveAI);
        CheckPawnPromotion(moveAI, theBoard, colour);
        //If there has been a capture, add the points to the player
        setPoints(theBoard, moveAI);
        //Return the move
        return moveAI;
    }
}
