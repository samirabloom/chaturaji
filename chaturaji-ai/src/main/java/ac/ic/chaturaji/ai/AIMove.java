package ac.ic.chaturaji.ai;

/**
 * @author dg3213
 */
public class AIMove implements Comparable<AIMove> {

    //------ Data Members ------//

    // Set default to negative sentinel values.
    private int piece = -1;                // The piece to be moved
    private int capturedPiece = -1;        // The piece that has been captured (includes colour)
    private int source = -1;               // Source square
    private int destination = -1;          // Destination square
    private int type = -1;                 // What type of move is it - i.e capture/regular
    private int promotionType = -1;        // Is a promotion possible, and if so what type?
    private boolean boatTriumph = false;   // Does the move result in a boat triumph?

    private int evaluationType;            // These are used by the AI when storing values in the
    private double score;                  // transposition tables, allows us to associate moves
    private int depth;                     // with scores.

    //------ Methods ------//

    // Constructor

    public AIMove() {
    }

    public AIMove(int MovPiece, int SrcSqr, int DestSqr) {
        piece = MovPiece;
        source = SrcSqr;
        destination = DestSqr;
    }

    // Accessors

    public int getDestination() {
        return destination;
    }

    public int getSource() {
        return source;
    }

    public int getPiece() {
        return piece;
    }

    public int getCaptured() {
        return capturedPiece;
    }

    public void setCaptured(int newCap) {
        capturedPiece = newCap;
    }

    public int getType() {
        return type;
    }

    public void setType(int moveType) {
        type = moveType;
    }

    public int getPromoType() {
        return promotionType;
    }

    public boolean getTriumph() {
        return boatTriumph;
    }

    public int getEvaluationType() {
        return evaluationType;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double evaluation) {
        score = evaluation;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void setPromotion(int promoType) {
        promotionType = promoType;
    }

    public void setBoatTriumph(boolean isTrue) {
        boatTriumph = isTrue;
    }

    public void setEvalType(int flag) {
        evaluationType = flag;
    }

    // Functions

    public boolean IsEqual(AIMove another) {
        if (piece != another.getPiece())
            return false;
        if (capturedPiece != another.getCaptured())
            return false;
        if (source != another.getSource())
            return false;
        if (destination != another.getDestination())
            return false;
        if (type != another.getType())
            return false;
        if (promotionType != another.promotionType)
            return false;
        if (boatTriumph != another.boatTriumph)
            return false;

        // Otherwise all data entries match
        return true;
    }

    public int compareTo(AIMove compareMove) {

        int otherType = ((AIMove) compareMove).getType();

        // Wish to order our moves list in descending order (all captures come first)
        return otherType - this.type;

    }

    public String Print() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Move: ");

        if (type != GameConstants.RESIGN) {
            stringBuilder.append(GameConstants.PieceStrings[piece]);
            stringBuilder.append(" [ ");
            stringBuilder.append(source);
            stringBuilder.append(", ");
            stringBuilder.append(destination);
            stringBuilder.append(" ] Type: ");
            stringBuilder.append(type);
            switch (capturedPiece) {
                case -1:
                case GameConstants.EMPTY_SQUARE:
                    break;
                default:
                    stringBuilder.append(" Captured Piece: ").append(capturedPiece);
                    break;
            }
        } else {
            stringBuilder.append("RESIGNATION!");
        }

        return stringBuilder.toString();
    }
}