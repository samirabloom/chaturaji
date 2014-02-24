package ac.ic.chaturaji.ai;

abstract public class Player_AI
{
    // Data Members
    int colour;
    int points;
    int type;
    int[] KingsCaptured;

    /*---- Methods ------*/

    // Constructor
    public Player_AI(int Points, int[] kings) {
        points = Points;
        KingsCaptured = kings;
    }

    // Accessors:
    int GetColour()
    {
        return colour;
    }
    int GetType() {
        return type;
    }

    // Functions:
    void SetColour( int col )
    {
        colour = col;
    }

    int GetPoints() { return points; }

    protected void setPoints(Board_AI theBoard, Move_AI theMove){
        int captured = theMove.getCaptured();
        switch (captured){
            case GameConstants.PAWN:
                points++;
                break;
            case GameConstants.BOAT:
                points+=2;
                break;
            case GameConstants.KNIGHT:
                points+=3;
                break;
            case GameConstants.ELEPHANT:
                points+=4;
                break;
            case GameConstants.KING:
                int kingColour = theBoard.FindColourPieceInSquare(theMove.getDest());
                if(checkKingsCaptured(kingColour) && theBoard.GetBitBoard(GameConstants.KING+colour) != 0)
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

    // Functions

    //public abstract Move_AI GetMove(Board_AI theBoard, int source, int dest);

    protected boolean checkKingsCaptured(int kingColour){
        KingsCaptured[kingColour] = 1;
        int totalKingsCaptured = 0;
        for(int i = 0; i < 4; i++)
            totalKingsCaptured++;
        if(totalKingsCaptured == 3 )
            return true;
        return false;
    }
}