package ac.ic.chaturaji.ai;
//import Project.Chaturaji.*;


/**
     * Created with IntelliJ IDEA.
     * User: vea11
     * Date: 10/02/14
     * Time: 19:21
     * To change this template use File | Settings | File Templates.
     */
public class GameConstants_AI {

    // Allows quick referral to pieces when constructing bitboards.
    public static int YELLOW = 0;
    public static int BLUE = 1;
    public static int RED = 2;
    public static int GREEN = 3;

    public static final int PAWN = 0;
    public static final int KNIGHT = 4;
    public static final int BOAT = 8;
    public static final int ELEPHANT = 12;
    public static final int KING = 16;

    public static final int YELLOW_PAWN = PAWN;
    public static final int YELLOW_KNIGHT = KNIGHT;
    public static final int YELLOW_BOAT = BOAT;
    public static final int YELLOW_ELEPHANT = ELEPHANT;
    public static final int YELLOW_KING = KING;

    public static final int BLUE_PAWN = PAWN + 1;
    public static final int BLUE_KNIGHT = KNIGHT + 1;
    public static final int BLUE_BOAT = BOAT + 1;
    public static final int BLUE_ELEPHANT = ELEPHANT + 1;
    public static final int BLUE_KING = KING + 1;

    public static final int RED_PAWN = PAWN + 2;
    public static final int RED_KNIGHT = KNIGHT + 2;
    public static final int RED_BOAT = BOAT + 2;
    public static final int RED_ELEPHANT = ELEPHANT + 2;
    public static final int RED_KING = KING + 2;

    public static final int GREEN_PAWN = PAWN + 3;
    public static final int GREEN_KNIGHT = KNIGHT + 3;
    public static final int GREEN_BOAT = BOAT + 3;
    public static final int GREEN_ELEPHANT = ELEPHANT + 3;
    public static final int GREEN_KING = KING + 3;

    public static final int EMPTY_SQUARE = 20;
    public static final int ALL_PIECES = 20;
    public static final int ALL_SQUARES = 64;

    // These index the boards used to represent all the pieces for a particular colour
    public static final int ALL_YELLOW_PIECES = ALL_PIECES;
    public static final int ALL_BLUE_PIECES = ALL_PIECES + 1;
    public static final int ALL_RED_PIECES = ALL_PIECES + 2;
    public static final int ALL_GREEN_PIECES = ALL_PIECES + 3;

    public static final int YELLOW_END_SQUARES = 24;
    public static final int BLUE_END_SQUARES = 25;
    public static final int RED_END_SQUARES = 26;
    public static final int GREEN_END_SQUARES = 27;

    // Chaturaji.Board of pawns (any colour) which correspond to certain promotion types
    public static final int KNIGHT_PAWNS = 28;
    public static final int BOAT_PAWNS = 29;
    public static final int ELEPHANT_PAWNS = 30;
    public static final int KING_PAWNS = 31;

    public static final int ALL_BITBOARDS = 32;

    //Possible kind of moves
    public static final int NORMAL_MOVE = 0;
    public static final int CAPTURE = 1;
    public static final int RESIGN = 2;

    /***************************************************************************
     * DATA MEMBERS
     **************************************************************************/

    // An array of bitfields, each of which contains the single bit associated
    // with a square in a bitboard
    public static long SquareBits[];

    // Private table of tokens (string representations) for all pieces
    public static String PieceStrings[];
    public static final String PlayerStrings[] = { "YELLOW", "BLUE", "RED", "GREEN"};
    // Data needed to compute the evaluation function

    public static int PieceValues[ ];

    // static member initialization
    static
    {
        // Build the SquareBits constants
        SquareBits = new long[ ALL_SQUARES ];
        for( int i = 0; i < ALL_SQUARES; i++ )
        {
            SquareBits[ i ] = ( 1L << i );
        }

        // Tokens representing the various concepts in the game, for printint
        // and file i/o purposes
        // PieceStrings contains an extra string representing empty squares
        PieceStrings = new String[ ALL_PIECES + 1 ];
        PieceStrings[ YELLOW_PAWN ] = "YP";
        PieceStrings[ YELLOW_ELEPHANT ] = "YR";
        PieceStrings[ YELLOW_KNIGHT ] = "YN";
        PieceStrings[ YELLOW_BOAT ] = "YB";
        PieceStrings[ YELLOW_KING ] = "YK";

        PieceStrings[ BLUE_PAWN ] = "BP";
        PieceStrings[ BLUE_ELEPHANT ] = "BR";
        PieceStrings[ BLUE_KNIGHT ] = "BN";
        PieceStrings[ BLUE_BOAT ] = "BB";
        PieceStrings[ BLUE_KING ] = "BK";

        PieceStrings[ RED_PAWN ] = "RP";
        PieceStrings[ RED_ELEPHANT ] = "RR";
        PieceStrings[ RED_KNIGHT ] = "RN";
        PieceStrings[ RED_BOAT ] = "RB";
        PieceStrings[ RED_KING ] = "RK";

        PieceStrings[ GREEN_PAWN ] = "GP";
        PieceStrings[ GREEN_ELEPHANT ] = "GR";
        PieceStrings[ GREEN_KNIGHT ] = "GN";
        PieceStrings[ GREEN_BOAT ] = "GB";
        PieceStrings[ GREEN_KING ] = "GK";

        PieceStrings[ ALL_PIECES ] = "  ";

        // Numerical evaluation of piece material values
        PieceValues = new int[ ALL_PIECES ];
        PieceValues[ YELLOW_PAWN ] = 150;
        PieceValues[ BLUE_PAWN ] = 150;
        PieceValues[ RED_PAWN ] = 150;
        PieceValues[ GREEN_PAWN ] = 150;

        PieceValues[ YELLOW_BOAT ] = 300;
        PieceValues[ BLUE_BOAT ] = 300;
        PieceValues[ RED_BOAT ] = 300;
        PieceValues[ GREEN_BOAT ] = 300;

        PieceValues[ YELLOW_KNIGHT ] = 350;
        PieceValues[ BLUE_KNIGHT ] = 350;
        PieceValues[ RED_KNIGHT ] = 350;
        PieceValues[ GREEN_KNIGHT ] = 350;

        PieceValues[ YELLOW_ELEPHANT ] = 500;
        PieceValues[ BLUE_ELEPHANT ] = 500;
        PieceValues[ RED_ELEPHANT ] = 500;
        PieceValues[ GREEN_ELEPHANT ] = 500;

        PieceValues[ YELLOW_KING ] = 1500;
        PieceValues[ BLUE_KING ] = 1500;
        PieceValues[ RED_KING ] = 1500;
        PieceValues[ GREEN_KING ] = 1500;
    }

}
