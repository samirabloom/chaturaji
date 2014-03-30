package ac.ic.chaturaji.ai;

import java.util.Set;

public class PlayerComp extends Player_AI {

    int colour;
    MoveGenerator_AI ValidMoves;
    Evaluation evalFunction;

    PlayerComp (int col, int points, Set<Integer> kingsCaptured){
        super(points, kingsCaptured);
        this.type = GameConstants.HUMAN;
        colour = col;
        ValidMoves = new MoveGenerator_AI();
        evalFunction = new Evaluation();
    }


    public int GetPlayerType(){ return GameConstants.AI; }

    public Move_AI GetMove(Board_AI board, int searchType){
        //Generate all the moves
        Move_AI move;
        MaxN maxn = new MaxN();
        AlphaBeta alphabeta  = new AlphaBeta();
        MTDF mtdf = new MTDF();

        //move = mtdf.Search(board);
        move = alphabeta.Search(board, board.GetCurrentPlayer(), 7);

        // Set the points if a capture occurred.
        if (move != null) {
            setPoints(board, move);
        }

        //Return the move
        return move;
    }
}