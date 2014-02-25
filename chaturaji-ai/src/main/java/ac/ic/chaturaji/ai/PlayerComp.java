package ac.ic.chaturaji.ai;

public class PlayerComp extends Player_AI {

    int colour;
    MoveGenerator_AI ValidMoves;

    PlayerComp (int col, int points, int[] kingsCaptured){
        super(points, kingsCaptured);
        this.type = GameConstants.HUMAN;
        colour = col;
        ValidMoves = new MoveGenerator_AI();
    }


    public int GetPlayerType(){ return GameConstants.AI; }

    public Move_AI GetMove(Board_AI theBoard){
        //Generate all the moves
        Move_AI move;
        move = ValidMoves.Search(theBoard);

        //Select the move
        //Move_AI move = (Move_AI)ValidMoves.GetMoves().get(0);

        // Set the points if a capture occurred.
        setPoints(theBoard, move);

        //Return the move
        return move;
    }
}