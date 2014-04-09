package ac.ic.chaturaji.ai;

import java.util.Set;

/**
 * @author dg3213
 */
abstract public class Player_AI {

    // Data Members

    int colour;
    int points;
    int type;
    Set<Integer> kingsCaptured;

    /*---- Methods ------*/

    // Constructor

    public Player_AI(int points, Set<Integer> kingsCaptured) {
        this.points = points;
        this.kingsCaptured = kingsCaptured;
    }

    // Accessors

    void SetColour(int col) {
        colour = col;
    }

    int GetPoints() {
        return points;
    }

    // Functions

    protected void setPoints(Board_AI theBoard, Move_AI theMove) {
        if (theMove != null) {
            switch (theMove.getCaptured()) {
                case GameConstants.PAWN:
                    points++;
                    break;
                case GameConstants.BOAT:
                    points += 2;
                    break;
                case GameConstants.KNIGHT:
                    points += 3;
                    break;
                case GameConstants.ELEPHANT:
                    points += 4;
                    break;
                case GameConstants.KING:
                    int kingColour = theBoard.FindColourPieceInSquare(theMove.getDest());
                    if (checkKingsCaptured(kingColour) && theBoard.GetBitBoard(GameConstants.KING + colour) != 0)
                        points += 54;
                    else
                        points += 5;
                    break;
                default:
                    break;
            }
            if (theMove.getTriumph()) {
                points += 6;
            }
        }
    }

    protected boolean checkKingsCaptured(int kingColour) {
        kingsCaptured.add(kingColour);
        return kingsCaptured.size() == 3;
    }
}