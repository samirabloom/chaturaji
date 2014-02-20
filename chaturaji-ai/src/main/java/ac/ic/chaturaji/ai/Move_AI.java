
package ac.ic.chaturaji.ai;
//import Project.Chaturaji.*;


public class Move_AI
{
    //------ Data Members ------//

    // Set default to negative sentinel values.
    private int Piece = -1;
    private int CapturedPiece = -1;
    private int Source = -1;
    private int Dest = -1;
    private int Type = -1;
    private int PromotionType = -1;
    private boolean BoatTriumph = false;


    //------ Methods ------//

    // Constructor:
    public Move_AI() { }

    public Move_AI(int MovPiece, int SrcSqr, int DestSqr) {
        Piece = MovPiece;
        Source = SrcSqr;
        Dest = DestSqr;
    }

    // Accessors:
    public int getDest() {return Dest;}
    public int getSource() {return Source;}
    public int getPiece() {return Piece;}
    public int getCaptured() {return CapturedPiece;}
    public int getType() {return Type;}
    public int getPromoType() {return PromotionType;}
    public boolean getTriumph() {return BoatTriumph;}

    public void SetDest(int newDest) {
	    Dest = newDest;
    }
    public void SetSource(int newSrc) {
	    Source = newSrc;
    }
    public void SetPiece(int newPc) {
	    Piece = newPc;
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

    // Functions:
    public boolean IsEqual(Move_AI another) {
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

    public void Print()
    {
        System.out.print( "Move: " );

        if (Type != GameConstants_AI.RESIGN)
        {
            System.out.print(GameConstants_AI.PieceStrings[Piece]);
            System.out.print(" [ ");
            System.out.print(Source);
            System.out.print(", ");
            System.out.print(Dest);
            System.out.print(" ] TYPE: ");
            System.out.println(Type);
        }
        else
        {
            System.out.println("RESIGNATION!");
        }
    }
}
