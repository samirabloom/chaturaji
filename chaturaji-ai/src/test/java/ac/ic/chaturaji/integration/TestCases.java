package ac.ic.chaturaji.integration;

import ac.ic.chaturaji.ai.GameConstants;

/**
 * @author dg3213
 */
public class TestCases {

    public static int[][] BoatTriumphGame;
    public static int[][] BoatPromo;
    public static int[][] KnightPromo;
    public static int[][] ElephantPromo;
    public static int[][] KingPromo;


    public static long Squares[];
    public static long[] JustYellows;
    public static long[] JustBlues;
    public static long[] JustReds;
    public static long[] JustGreens;

    public static long[] YellowPawns;
    public static long[] BluePawns;
    public static long[] RedPawns;
    public static long[] GreenPawns;

    // Initialise the test cases:
    static {

        Squares = new long[64];
        for (int i = 0; i < 64; i++) {
            Squares[i] = (1L << i);
        }

        BoatTriumphGame = new int[][]{{0, 18}, {7, 21}, {63, 45}, {56, 42}, {18, 36}, {21, 35}, {45, 27}, {42, 28}};

        JustYellows = new long[32];
        JustBlues = new long[32];
        JustReds = new long[32];
        JustGreens = new long[32];
        YellowPawns = new long[32];
        BluePawns = new long[32];
        RedPawns = new long[32];
        GreenPawns = new long[32];

        EmptyBoards();
        SinglePieceBoards();

        BoatPromo = new int[][]{{1, 2}, {15, 23}, {62, 61}, {48, 40}, {2, 3}, {23, 31}, {61, 60},
                {40, 32}, {3, 4}, {31, 39}, {60, 59}, {32, 24}, {4, 5}, {39, 47}, {59, 58}, {24, 16},
                {5, 6}, {47, 55}, {58, 57}, {16, 8}, {6, 7}, {55, 63}, {57, 56}, {8, 0}};

        KnightPromo = new int[][]{{9, 10}, {14, 22}, {54, 53}, {49, 41}, {10, 11}, {22, 30}, {53, 52}, {41, 33}, {11, 12}, {30, 38}, {52, 51},
                {33, 25}, {12, 13}, {38, 46}, {51, 50}, {25, 17}, {13, 14}, {46, 54}, {50, 49}, {17, 9}, {14, 15}, {54, 62}, {49, 48}, {9, 1}};

        ElephantPromo = new int[][]{{17, 18}, {13, 21}, {46, 45}, {50, 42}, {18, 19}, {21, 29}, {45, 44}, {42, 34}, {19, 20}, {29, 37},
                {44, 43}, {34, 26}, {20, 21}, {37, 45}, {43, 42}, {26, 18}, {21, 22}, {45, 53}, {42, 41}, {18, 10}, {22, 23}, {53, 61}, {41, 40}, {10, 2}};

        KingPromo = new int[][]{{25, 26}, {12, 20}, {38, 37}, {51, 43}, {26, 27}, {20, 28}, {37, 36}, {43, 35}, {27, 28}, {28, 36}, {36, 35},
                {35, 27}, {28, 29}, {36, 44}, {35, 34}, {27, 19}, {29, 30}, {44, 52}, {34, 33}, {19, 11}, {30, 31}, {52, 60}, {33, 32}, {11, 3}};
    }

    private static void setYellows() {
        JustYellows[GameConstants.YELLOW_PAWN] = (Squares[1] | Squares[9] | Squares[17] | Squares[25]);

        for (int i = 7; i <= 63; i = i + 8) {
            JustYellows[GameConstants.YELLOW_END_SQUARES] |= Squares[i];
        }

        JustYellows[GameConstants.BOAT_PAWNS] = (Squares[1]);
        JustYellows[GameConstants.KNIGHT_PAWNS] = (Squares[9]);
        JustYellows[GameConstants.ELEPHANT_PAWNS] = (Squares[17]);
        JustYellows[GameConstants.KING_PAWNS] = (Squares[25]);

        copy(YellowPawns, JustYellows);
        YellowPawns[GameConstants.ALL_YELLOW_PIECES] = YellowPawns[GameConstants.YELLOW_PAWN];

        JustYellows[GameConstants.YELLOW_BOAT] = Squares[0];
        JustYellows[GameConstants.YELLOW_KNIGHT] = Squares[8];
        JustYellows[GameConstants.YELLOW_ELEPHANT] = Squares[16];
        JustYellows[GameConstants.YELLOW_KING] = Squares[24];

        JustYellows[GameConstants.ALL_YELLOW_PIECES] = (JustYellows[GameConstants.YELLOW_PAWN] | JustYellows[GameConstants.YELLOW_BOAT]
                | JustYellows[GameConstants.YELLOW_KNIGHT] | JustYellows[GameConstants.YELLOW_ELEPHANT] | JustYellows[GameConstants.YELLOW_KING]);

    }

    private static void setBlues() {
        JustBlues[GameConstants.BLUE_PAWN] = (Squares[12] | Squares[13] | Squares[14] | Squares[15]);

        for (int i = 56; i <= 63; i++) {
            JustBlues[GameConstants.BLUE_END_SQUARES] |= Squares[i];
        }

        JustBlues[GameConstants.BOAT_PAWNS] = Squares[15];
        JustBlues[GameConstants.KNIGHT_PAWNS] = Squares[14];
        JustBlues[GameConstants.ELEPHANT_PAWNS] = Squares[13];
        JustBlues[GameConstants.KING_PAWNS] = Squares[12];

        copy(BluePawns, JustBlues);
        BluePawns[GameConstants.ALL_BLUE_PIECES] = BluePawns[GameConstants.BLUE_PAWN];

        JustBlues[GameConstants.BLUE_BOAT] = Squares[7];
        JustBlues[GameConstants.BLUE_KNIGHT] = Squares[6];
        JustBlues[GameConstants.BLUE_ELEPHANT] = Squares[5];
        JustBlues[GameConstants.BLUE_KING] = Squares[4];

        JustBlues[GameConstants.ALL_BLUE_PIECES] = (JustBlues[GameConstants.BLUE_PAWN] | JustBlues[GameConstants.BLUE_BOAT]
                | JustBlues[GameConstants.BLUE_KNIGHT] | JustBlues[GameConstants.BLUE_ELEPHANT] | JustBlues[GameConstants.BLUE_KING]);

    }

    private static void setReds() {
        JustReds[GameConstants.RED_PAWN] = (Squares[38] | Squares[46] | Squares[54] | Squares[62]);

        for (int i = 0; i <= 56; i = i + 8) {
            JustReds[GameConstants.RED_END_SQUARES] |= Squares[i];
        }

        JustReds[GameConstants.BOAT_PAWNS] = Squares[62];
        JustReds[GameConstants.KNIGHT_PAWNS] = Squares[54];
        JustReds[GameConstants.ELEPHANT_PAWNS] = Squares[46];
        JustReds[GameConstants.KING_PAWNS] = Squares[38];

        copy(RedPawns, JustReds);
        RedPawns[GameConstants.ALL_RED_PIECES] = RedPawns[GameConstants.RED_PAWN];

        JustReds[GameConstants.RED_BOAT] = Squares[63];
        JustReds[GameConstants.RED_KNIGHT] = Squares[55];
        JustReds[GameConstants.RED_ELEPHANT] = Squares[47];
        JustReds[GameConstants.RED_KING] = Squares[39];

        JustReds[GameConstants.ALL_RED_PIECES] = (JustReds[GameConstants.RED_PAWN] | JustReds[GameConstants.RED_BOAT]
                | JustReds[GameConstants.RED_KNIGHT] | JustReds[GameConstants.RED_ELEPHANT] | JustReds[GameConstants.RED_KING]);

    }

    private static void setGreens() {
        JustGreens[GameConstants.GREEN_PAWN] = (Squares[48] | Squares[49] | Squares[50] | Squares[51]);

        for (int i = 0; i <= 7; i++) {
            JustGreens[GameConstants.GREEN_END_SQUARES] |= Squares[i];
        }

        JustGreens[GameConstants.BOAT_PAWNS] = Squares[48];
        JustGreens[GameConstants.KNIGHT_PAWNS] = Squares[49];
        JustGreens[GameConstants.ELEPHANT_PAWNS] = Squares[50];
        JustGreens[GameConstants.KING_PAWNS] = Squares[51];

        copy(GreenPawns, JustGreens);
        GreenPawns[GameConstants.ALL_GREEN_PIECES] = GreenPawns[GameConstants.GREEN_PAWN];

        JustGreens[GameConstants.GREEN_BOAT] = Squares[56];
        JustGreens[GameConstants.GREEN_KNIGHT] = Squares[57];
        JustGreens[GameConstants.GREEN_ELEPHANT] = Squares[58];
        JustGreens[GameConstants.GREEN_KING] = Squares[49];

        JustGreens[GameConstants.ALL_GREEN_PIECES] = (JustGreens[GameConstants.GREEN_PAWN] | JustGreens[GameConstants.GREEN_BOAT]
                | JustGreens[GameConstants.GREEN_KNIGHT] | JustGreens[GameConstants.GREEN_ELEPHANT] | JustGreens[GameConstants.GREEN_KING]);

    }

    private static void SinglePieceBoards() {
        setYellows();
        setBlues();
        setGreens();
        setReds();
    }

    private static void EmptyBoards() {
        for (int i = 0; i < 32; i++) {
            JustYellows[i] = 0;
            JustBlues[i] = 0;
            JustReds[i] = 0;
            JustGreens[i] = 0;
        }
    }

    private static void copy(long[] board1, long[] board2) {
        for (int i = 0; i < 32; i++)
            board1[i] = board2[i];
    }
}
