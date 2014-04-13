package ac.ic.chaturaji.ai;

/**
 * @author dg3213
 */
public class AIMove {

    //------ Data Members ------//

    // Set default to negative sentinel values.
    private int piece = -1;
    private int capturedPiece = -1;
    private int source = -1;
    private int destination = -1;
    private int type = -1;
    private int promotionType = -1;
    private boolean boatTriumph = false;

    private int evaluationType;
    private double score;
    private int depth;

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

    public int getType() {
        return type;
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

    public int getDepth() {
        return depth;
    }

    public void setCaptured(int newCap) {
        capturedPiece = newCap;
    }

    public void setType(int moveType) {
        type = moveType;
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

    public void setScore(double evaluation) {
        score = evaluation;
    }

    public void setDepth(int depth) {
        this.depth = depth;
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