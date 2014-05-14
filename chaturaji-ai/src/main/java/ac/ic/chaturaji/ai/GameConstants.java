package ac.ic.chaturaji.ai;

import java.util.Random;

/**
 * @author dg3213
 */
public class GameConstants {

    /*------ BOARD CONSTANTS ------*/

    // Allows quick referral to pieces when constructing bit boards.
    public static final int YELLOW = 0;
    public static final int BLUE = 1;
    public static final int RED = 2;
    public static final int GREEN = 3;

    public static final int PAWN = 0;
    public static final int YELLOW_PAWN = PAWN;
    public static final int BLUE_PAWN = PAWN + 1;
    public static final int RED_PAWN = PAWN + 2;
    public static final int GREEN_PAWN = PAWN + 3;
    public static final int KNIGHT = 4;
    public static final int YELLOW_KNIGHT = KNIGHT;
    public static final int BLUE_KNIGHT = KNIGHT + 1;
    public static final int RED_KNIGHT = KNIGHT + 2;
    public static final int GREEN_KNIGHT = KNIGHT + 3;
    public static final int BOAT = 8;
    public static final int YELLOW_BOAT = BOAT;
    public static final int BLUE_BOAT = BOAT + 1;
    public static final int RED_BOAT = BOAT + 2;
    public static final int GREEN_BOAT = BOAT + 3;
    public static final int ELEPHANT = 12;
    public static final int YELLOW_ELEPHANT = ELEPHANT;
    public static final int BLUE_ELEPHANT = ELEPHANT + 1;
    public static final int RED_ELEPHANT = ELEPHANT + 2;
    public static final int GREEN_ELEPHANT = ELEPHANT + 3;
    public static final int KING = 16;
    public static final int YELLOW_KING = KING;
    public static final int BLUE_KING = KING + 1;
    public static final int RED_KING = KING + 2;
    public static final int GREEN_KING = KING + 3;

    public static final int EMPTY_SQUARE = 20;
    public static final int ALL_PIECES = 20;
    // These index the boards used to represent all the pieces for a particular colour
    public static final int ALL_YELLOW_PIECES = ALL_PIECES;
    public static final int ALL_BLUE_PIECES = ALL_PIECES + 1;
    public static final int ALL_RED_PIECES = ALL_PIECES + 2;
    public static final int ALL_GREEN_PIECES = ALL_PIECES + 3;
    public static final int ALL_SQUARES = 64;
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

    //Material value of each piece
    public static final int PAWN_VALUE = 100;
    public static final int KNIGHT_VALUE = 500;
    public static final int BOAT_VALUE = 300;
    public static final int ELEPHANT_VALUE = 800;
    public static final int KING_VALUE = 1300;

    /*------ MOVE CONSTANTS ------*/
    //Possible kind of moves
    public static final int NORMAL_MOVE = 0;
    public static final int CAPTURE = 1;
    public static final int RESIGN = 2;
    public static final int EXACT_VALUE = 0;
    public static final int UPPER_BOUND = 1;
    public static final int LOWER_BOUND = 2;
    public static final int HUMAN = 0;

    /*------ PLAYER CONSTANTS ------*/
    public static final int AI = 1;
    public static final String PlayerStrings[] = {"Yellow", "Blue", "Red", "Green"};
    public static final long YellowMove;
    public static final long BlueMove;
    public static final long RedMove;
    public static final long GreenMove;

    /*------ OTHER CONSTANTS ------*/

    // An array of bitfields, each of which contains the single bit associated
    // with a square in a bitboard
    public static long SquareBits[];

    // Used for printing:
    public static String PieceStrings[];
    public static long ZobristHash[][];
    public static long ZobristLock[][];

    /*------ EVALUATION FUNCTION CONSTANTS ------*/

    // Please do not change the formatting as it is a lot easier to see correspondence
    // between board and position score like this

    public static double YellowPawnTable[] = {
            0,  0,   0, 1.5, 1.5,   2, 5,  7,
            0,  0,   0, 1.5, 1.5,   2, 5,  7,
            0, -2, 1.5,   2,   2, 2.5, 5,  7,
            0, -2, 1.5, 2.5, 2.5, 2.5, 5,  7,
            0,  0,   0, 2.5, 2.5,   2, 3,  7,
            0,  0,   0,   0,   0,   0, 3,  7,
            0,  0,   0,   0,   0,   0, 3,  7,
            0,  0,   0,   0,   0,   0, 3,  7
    };

    public static double BluePawnTable[] = {
            0, 0, 0,   0,   0,   0,   0,   0,
            0, 0, 0,   0,  -2,  -2,   0,   0,
            0, 0, 0,   0, 1.5, 1.5,   0,   0,
            0, 0, 0, 2.5, 2.5,   2, 1.5, 1.5,
            0, 0, 0, 2.5, 2.5,   2, 1.5, 1.5,
            0, 0, 0,   2, 2.5,   2,   2,   2,
            3, 3, 3,   3,   5,   5,   5,   5,
            7, 7, 7,   7,   7,   7,   7,   7
    };

    public static double RedPawnTable[] = {
            7, 3,   0,   0,   0,   0,  0, 0,
            7, 3,   0,   0,   0,   0,  0, 0,
            7, 3,   0,   0,   0,   0,  0, 0,
            7, 3,   2, 2.5, 2.5,   0,  0, 0,
            7, 5, 2.5, 2.5, 2.5, 1.5, -2, 0,
            7, 5, 2.5,   2,   2, 1.5, -2, 0,
            7, 5,   2, 1.5, 1.5,   0,  0, 0,
            7, 5,   2, 1.5, 1.5,   0,  0, 0
    };

    public static double GreenPawnTable[] = {
            7,   7,   7,   7,   7, 7, 7, 7,
            5,   5,   5,   5,   3, 3, 3, 3,
            2,   2,   2, 2.5,   2, 0, 0, 0,
          1.5, 1.5, 2.5, 2.5, 2.5, 0, 0, 0,
          1.5, 1.5,   2, 2.5, 2.5, 0, 0, 0,
            0,   0, 1.5, 1.5,   0, 0, 0, 0,
            0,   0,  -2,  -2,   0, 0, 0, 0,
            0,   0,   0,   0,   0, 0, 0, 0
    };

    public static double KnightTable[] = {
            -50,-40,-30,-30,-30,-30,-40,-50,
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-20,-30,-30,-20,-40,-50,
    };

    public static double BoatTable[] = {
            -20,-10,-10,-10,-10,-10,-10,-20,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10, 10, 10, 10, 10, 10, 10,-10,
            -10,  5,  0,  0,  0,  0,  5,-10,
            -20,-10,-40,-10,-10,-40,-10,-20,
    };


    /*------ MEMBER INITIALISATION ------*/
    // static member initialization
    static {
        // Build the SquareBits constants
        SquareBits = new long[ALL_SQUARES];
        for (int i = 0; i < ALL_SQUARES; i++) {
            SquareBits[i] = (1L << i);
        }

        Random rnd = new Random();
        ZobristHash = new long[GameConstants.ALL_PIECES][64];
        ZobristLock = new long[GameConstants.ALL_PIECES][64];

        YellowMove = rnd.nextLong();
        BlueMove = rnd.nextLong();
        RedMove = rnd.nextLong();
        GreenMove = rnd.nextLong();

        for (int player = 0; player < 4; player++) {
            for (int piece = 0; piece < GameConstants.ALL_PIECES; piece++)
                for (int square = 0; square < 64; square++) {
                    ZobristHash[piece][square] = rnd.nextLong();
                    ZobristLock[piece][square] = rnd.nextLong();
                }
        }

        // Tokens representing the various concepts in the game, for printing
        // and file i/o purposes.
        // PieceStrings contains an extra string representing empty squares.
        PieceStrings = new String[ALL_PIECES + 1];
        PieceStrings[YELLOW_PAWN] = "YP";
        PieceStrings[YELLOW_ELEPHANT] = "YE";
        PieceStrings[YELLOW_KNIGHT] = "YN";
        PieceStrings[YELLOW_BOAT] = "YB";
        PieceStrings[YELLOW_KING] = "YK";

        PieceStrings[BLUE_PAWN] = "BP";
        PieceStrings[BLUE_ELEPHANT] = "BE";
        PieceStrings[BLUE_KNIGHT] = "BN";
        PieceStrings[BLUE_BOAT] = "BB";
        PieceStrings[BLUE_KING] = "BK";

        PieceStrings[RED_PAWN] = "RP";
        PieceStrings[RED_ELEPHANT] = "RE";
        PieceStrings[RED_KNIGHT] = "RN";
        PieceStrings[RED_BOAT] = "RB";
        PieceStrings[RED_KING] = "RK";

        PieceStrings[GREEN_PAWN] = "GP";
        PieceStrings[GREEN_ELEPHANT] = "GE";
        PieceStrings[GREEN_KNIGHT] = "GN";
        PieceStrings[GREEN_BOAT] = "GB";
        PieceStrings[GREEN_KING] = "GK";

        PieceStrings[ALL_PIECES] = "  ";
    }
}
