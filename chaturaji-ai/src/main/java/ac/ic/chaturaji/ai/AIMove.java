package ac.ic.chaturaji.ai;

/**
 * @author dg3213
 */
public class AIMove {

    //------ Data Members ------//

    // Set default to negative sentinel values.
    private int Piece = -1;
    private int CapturedPiece = -1;
    private int Source = -1;
    private int Dest = -1;
    private int Type = -1;
    private int PromotionType = -1;
    private boolean BoatTriumph = false;

    private int EvaluationType;
    private double Score;
    private int Depth;

    //------ Methods ------//

    // Constructor

    public AIMove() {
    }

    public AIMove(int MovPiece, int SrcSqr, int DestSqr) {
        Piece = MovPiece;
        Source = SrcSqr;
        Dest = DestSqr;
    }

    // Accessors

    public int getDest() {
        return Dest;
    }

    public int getSource() {
        return Source;
    }

    public int getPiece() {
        return Piece;
    }

    public int getCaptured() {
        return CapturedPiece;
    }

    public int getType() {
        return Type;
    }

    public int getPromoType() {
        return PromotionType;
    }

    public boolean getTriumph() {
        return BoatTriumph;
    }

    public int getEvaluationType() {
        return EvaluationType;
    }

    public double getScore() {
        return Score;
    }

    public int getDepth() {
        return Depth;
    }

    public void SetCaptured(int newCap) {
        CapturedPiece = newCap;
    }

    public void SetType(int moveType) {
        Type = moveType;
    }

    public void SetPromotion(int promoType) {
        PromotionType = promoType;
    }

    public void SetBoatTriumph(boolean isTrue) {
        BoatTriumph = isTrue;
    }

    public void SetEvalType(int flag) {
        EvaluationType = flag;
    }

    public void SetScore(double evaluation) {
        Score = evaluation;
    }

    public void SetDepth(int depth) {
        Depth = depth;
    }

    // Functions

    public boolean IsEqual(AIMove another) {
        if (Piece != another.getPiece())
            return false;
        if (CapturedPiece != another.getCaptured())
            return false;
        if (Source != another.getSource())
            return false;
        if (Dest != another.getDest())
            return false;
        if (Type != another.getType())
            return false;
        if (PromotionType != another.PromotionType)
            return false;
        if (BoatTriumph != another.BoatTriumph)
            return false;

        // Otherwise all data entries match
        return true;
    }

    public String Print() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Move: ");

        if (Type != GameConstants.RESIGN) {
            stringBuilder.append(GameConstants.PieceStrings[Piece]);
            stringBuilder.append(" [ ");
            stringBuilder.append(Source);
            stringBuilder.append(", ");
            stringBuilder.append(Dest);
            stringBuilder.append(" ] Type: ");
            stringBuilder.append(Type);
            switch (CapturedPiece) {
                case -1:
                case GameConstants.EMPTY_SQUARE:
                    break;
                default:
                    stringBuilder.append(" Captured Piece: ").append(CapturedPiece);
                    break;
            }
        } else {
            stringBuilder.append("RESIGNATION!");
        }

        return stringBuilder.toString();
    }
}