package ac.ic.chaturaji.ai;
//import Project.Chaturaji.*;


abstract public class Player_AI
{
    // Data Members
	int colour;
    int points;
    int[] KingsCaptured;

	// Constructor
	public Player_AI(int Points, int[] kings) {
        points = Points;
        KingsCaptured = kings;
    }

	// Accessors
	int GetColour()
	{
		return colour;
	}
	void SetColour( int col )
	{
		colour = col;
	}

    int GetPoints() { return points; }
    protected void setPoints(Board_AI theBoard, Move_AI theMove){
        int captured = theMove.getCaptured();
        switch (captured){
            case GameConstants_AI.PAWN:
                points++;
                break;
            case GameConstants_AI.BOAT:
                points+=2;
                break;
            case GameConstants_AI.KNIGHT:
                points+=3;
                break;
            case GameConstants_AI.ELEPHANT:
                points+=4;
                break;
            case GameConstants_AI.KING:
                int kingColour = theBoard.FindColourPieceInSquare(theMove.getDest());
                if(checkKingsCaptured(kingColour) && theBoard.GetBitBoard(GameConstants_AI.KING+colour) != 0)
                    points+= 54;
                else
                    points+=5;
                break;
            default:
                break;
        }
        if(theMove.getTriumph())
            points+=6;
    }

    //Helper functions
    protected void CheckPawnPromotion(Move_AI Mov, Board_AI theBoard, int colour) {
        boolean is_pawn = (Mov.getPiece() == GameConstants_AI.PAWN + colour);
        boolean reached_end = ((Mov.getDest() & theBoard.GetBitBoard(GameConstants_AI.YELLOW_END_SQUARES + colour)) != 0);

        if (is_pawn && reached_end)
        {
            if((Mov.getSource() & theBoard.GetBitBoard(GameConstants_AI.KNIGHT_PAWNS)) != 0 )
                Mov.SetPromotion(GameConstants_AI.KNIGHT);
            else if( (Mov.getSource() & theBoard.GetBitBoard(GameConstants_AI.BOAT_PAWNS)) != 0 )
                Mov.SetPromotion(GameConstants_AI.BOAT);
            else if( (Mov.getSource() & theBoard.GetBitBoard(GameConstants_AI.ELEPHANT_PAWNS)) != 0  )
                Mov.SetPromotion(GameConstants_AI.ELEPHANT);
            else
                Mov.SetPromotion(GameConstants_AI.KING);
        }
    }

    protected boolean checkKingsCaptured(int kingColour){
        KingsCaptured[kingColour] = 1;
        int totalKingsCaptured = 0;
        for(int i = 0; i < 4; i++)
            totalKingsCaptured++;
        if(totalKingsCaptured == 3 )
            return true;
        return false;
    }

    // Functions
	//public abstract Move_AI GetMove(Board_AI theBoard);
}
