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

    public Move_AI GetMove(Board_AI theBoard, int searchType){
        //Generate all the moves
        Move_AI move;
        MaxN maxn = new MaxN();
        AlphaBeta alphabeta  = new AlphaBeta();

        searchType = 0;
        if (searchType == 0)
            move = alphabeta.Search(theBoard, theBoard.GetCurrentPlayer(), 7);
        else
            move = maxn.Search(theBoard, theBoard.GetCurrentPlayer(), 7);

        // Set the points if a capture occurred.
        if (move != null)
            setPoints(theBoard, move);

        //Return the move
        return move;
    }
}