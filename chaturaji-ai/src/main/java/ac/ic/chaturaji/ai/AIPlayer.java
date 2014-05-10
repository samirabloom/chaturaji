package ac.ic.chaturaji.ai;

import java.util.Set;

/**
 * @author dg3213
 */
abstract public class AIPlayer {

    // Data Members

    int colour;
    int points;
    int type;
    Set<Integer> kingsCaptured;

    /*---- Methods ------*/

    // Constructor

    public AIPlayer(int points, Set<Integer> kingsCaptured) {
        this.points = points;
        this.kingsCaptured = kingsCaptured;
    }

    // Accessors

    void SetColour(int col) {
        colour = col;
    }

    int getPoints() {
        return points;
    }

    // Functions

    protected void setPoints(AIBoard theBoard, AIMove theMove) {
        if (theMove != null) {
            switch (theMove.getCaptured()/4) {
                case GameConstants.PAWN/4:
                    points++;
                    break;
                case GameConstants.BOAT/4:
                    points += 2;
                    break;
                case GameConstants.KNIGHT/4:
                    points += 3;
                    break;
                case GameConstants.ELEPHANT/4:
                    points += 4;
                    break;
                case GameConstants.KING/4:
                    int kingColour = theBoard.FindColourPieceInSquare(theMove.getDestination());
                    if (checkKingsCaptured(kingColour) && theBoard.getBitBoard(GameConstants.KING + colour) != 0) {
                        points += 54;
                    } else {
                        points += 5;
                    }
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