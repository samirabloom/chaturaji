package ac.ic.chaturaji.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chaturaji.Board_AI: constructs all relevant bitboards and handles chaturaji board manipulation (i.e. adding and removing pieces).
 * The board is represented by a 64-bit string, the first bit denoting square a8, the second is a7 and so forth.
 * There are 32 bitboards in total. The first 20 are for each piece of each player; for example Green will have 5 bitboards,
 * one for each of its different pieces. The next 4 are used to denote the positions of ALL the individual players' pieces,
 * essentially just the 5 single bitboards concatenated into one.
 * The following 4 are the end squares opposite to each colour's starting position. These are the squares on which the pawns may
 * promote to a higher value piece given that their corresponding piece has been taken.
 * The final four are used to keep track of which pawns are potential boats/elephants/kings/knights upon promotion.
 *
 * @author dg3213
 */
public class AIBoard implements Cloneable {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /*------ Data Members ------*/

    // The actual data representation of a chaturaji board.  First, an array of
    // bitboards, each of which contains flags for the squares where you can
    // find a specific type of piece
    private long BitBoards[];

    private int MaterialValue[];
    // Who is currently playing - 0 if yellow, 1 if blue etc.
    int CurrentPlayer;

	/*------ Methods ------*/

    /* Constructor */
    public AIBoard() {
        BitBoards = new long[GameConstants.ALL_BITBOARDS];
        MaterialValue = new int[4];

        StartBoard();
        EvalMaterial();
    }

    public AIBoard clone() {
        AIBoard cloned = new AIBoard();
        cloned.BitBoards = BitBoards.clone();
        cloned.MaterialValue = MaterialValue.clone();
        cloned.CurrentPlayer = CurrentPlayer;
        return cloned;
    }

    public AIBoard(long[] bit_boards, int colour) {
        BitBoards = bit_boards;
        MaterialValue = new int[4];
        EvalMaterial();

        CurrentPlayer = colour;
    }

    //Copy constructor
    public AIBoard(AIBoard board) {
        BitBoards = board.BitBoards;
        MaterialValue = board.MaterialValue;
        CurrentPlayer = board.CurrentPlayer;
    }

    /* Accessors */

    public int getCurrentPlayer() {
        return CurrentPlayer;
    }

    public long getBitBoard(int which_one) {
        return BitBoards[which_one];
    }

    public long[] GetBitBoards() {
        return BitBoards;
    }

    public int[] GetMaterialValue() {
        return MaterialValue;
    }

    public int GetMaterialValue(int colour) {
        return MaterialValue[colour];
    }
    /* Functions */

    // Initialise the Board:
    private void StartBoard() {
        // Empty the board of anything that may be on it.
        EmptyBoard();

        AddPiece(0, GameConstants.YELLOW_BOAT);
        AddPiece(8, GameConstants.YELLOW_KNIGHT);
        AddPiece(16, GameConstants.YELLOW_ELEPHANT);
        AddPiece(24, GameConstants.YELLOW_KING);

        AddPiece(4, GameConstants.BLUE_KING);
        AddPiece(5, GameConstants.BLUE_ELEPHANT);
        AddPiece(6, GameConstants.BLUE_KNIGHT);
        AddPiece(7, GameConstants.BLUE_BOAT);

        AddPiece(39, GameConstants.RED_KING);
        AddPiece(47, GameConstants.RED_ELEPHANT);
        AddPiece(55, GameConstants.RED_KNIGHT);
        AddPiece(63, GameConstants.RED_BOAT);

        AddPiece(56, GameConstants.GREEN_BOAT);
        AddPiece(57, GameConstants.GREEN_KNIGHT);
        AddPiece(58, GameConstants.GREEN_ELEPHANT);
        AddPiece(59, GameConstants.GREEN_KING);

        for (int i = 1; i <= 25; i = i + 8) {
            AddPiece(i, GameConstants.YELLOW_PAWN);
        }
        for (int i = 12; i <= 15; i++) {
            AddPiece(i, GameConstants.BLUE_PAWN);
        }
        for (int i = 38; i <= 62; i = i + 8) {
            AddPiece(i, GameConstants.RED_PAWN);
        }
        for (int i = 48; i <= 51; i++) {
            AddPiece(i, GameConstants.GREEN_PAWN);
        }

        for (int i = 7; i <= 63; i = i + 8) {
            BitBoards[GameConstants.YELLOW_END_SQUARES] |= GameConstants.SquareBits[i];
        }
        for (int i = 56; i <= 63; i++) {
            BitBoards[GameConstants.BLUE_END_SQUARES] |= GameConstants.SquareBits[i];
        }
        for (int i = 0; i <= 56; i = i + 8) {
            BitBoards[GameConstants.RED_END_SQUARES] |= GameConstants.SquareBits[i];
        }
        for (int i = 0; i <= 7; i++) {
            BitBoards[GameConstants.GREEN_END_SQUARES] |= GameConstants.SquareBits[i];
        }

        for (int i = 0; i < 4; i++) {
            BitBoards[GameConstants.ALL_YELLOW_PIECES + i] = (BitBoards[GameConstants.YELLOW_PAWN + i] | BitBoards[GameConstants.YELLOW_BOAT + i]
                    | BitBoards[GameConstants.YELLOW_KNIGHT + i] | BitBoards[GameConstants.YELLOW_ELEPHANT + i] | BitBoards[GameConstants.YELLOW_KING + i]);
        }

        // These bitboards represent the different types of pawn promotion. For example, the bitboard BOAT_PAWNS contains all pawns that
        // may promote to a boat piece upon reaching their end squares.
        BitBoards[GameConstants.BOAT_PAWNS] = (GameConstants.SquareBits[1] | GameConstants.SquareBits[15] | GameConstants.SquareBits[48] | GameConstants.SquareBits[62]);
        BitBoards[GameConstants.KNIGHT_PAWNS] = (GameConstants.SquareBits[9] | GameConstants.SquareBits[14] | GameConstants.SquareBits[49] | GameConstants.SquareBits[54]);
        BitBoards[GameConstants.ELEPHANT_PAWNS] = (GameConstants.SquareBits[17] | GameConstants.SquareBits[13] | GameConstants.SquareBits[50] | GameConstants.SquareBits[46]);
        BitBoards[GameConstants.KING_PAWNS] = (GameConstants.SquareBits[25] | GameConstants.SquareBits[12] | GameConstants.SquareBits[51] | GameConstants.SquareBits[38]);

        // Player to go first is always yellow
        CurrentPlayer = GameConstants.YELLOW;
    }

    private void EvalMaterial() {
        MaterialValue = new int[4];
        for (int i = 0; i < 4; i++) {
            //Check for the pawns of that colour
            if ((BitBoards[GameConstants.PAWN + i] & BitBoards[GameConstants.KING_PAWNS]) != 0)
                MaterialValue[i] += GameConstants.PAWN_VALUE;

            if ((BitBoards[GameConstants.PAWN + i] & BitBoards[GameConstants.KNIGHT_PAWNS]) != 0)
                MaterialValue[i] += GameConstants.PAWN_VALUE;

            if ((BitBoards[GameConstants.PAWN + i] & BitBoards[GameConstants.BOAT_PAWNS]) != 0)
                MaterialValue[i] += GameConstants.PAWN_VALUE;

            if ((BitBoards[GameConstants.PAWN + i] & BitBoards[GameConstants.ELEPHANT_PAWNS]) != 0)
                MaterialValue[i] += GameConstants.PAWN_VALUE;

            //Check for the elephant
            if ((BitBoards[GameConstants.ELEPHANT + i]) != 0)
                MaterialValue[i] += GameConstants.ELEPHANT_VALUE;

            //Check for the boat
            if ((BitBoards[GameConstants.BOAT + i]) != 0)
                MaterialValue[i] += GameConstants.BOAT_VALUE;

            //Check for the knight
            if ((BitBoards[GameConstants.KNIGHT + i]) != 0)
                MaterialValue[i] += GameConstants.KNIGHT_VALUE;

            //Check for the king
            if ((BitBoards[GameConstants.KING + i]) != 0)
                MaterialValue[i] += GameConstants.KING_VALUE;
        }

    }

    public long ZobristKey() {
        long zobristKey = 0;

        for (int piece = 0; piece < GameConstants.ALL_PIECES; piece++) {
            long currentBoard = BitBoards[piece];
            for (int square = 0; square < 64; square++) {
                if ((currentBoard & GameConstants.SquareBits[square]) != 0)
                    zobristKey ^= GameConstants.ZobristHash[piece][square];
            }
        }
        switch (CurrentPlayer) {
            case GameConstants.YELLOW:
                zobristKey ^= GameConstants.YellowMove;
                break;
            case GameConstants.BLUE:
                zobristKey ^= GameConstants.BlueMove;
                break;
            case GameConstants.RED:
                zobristKey ^= GameConstants.RedMove;
                break;
            case GameConstants.GREEN:
                zobristKey ^= GameConstants.GreenMove;
                break;
        }
        return zobristKey;
    }

    public int isGameOver() {
        int count = 0;

        for (int i = 0; i < 4; i++) {
            if (BitBoards[GameConstants.KING + i] != 0) {
                count++;
            }
        }
        return count;
    }

    // Look for the piece located on a specific square
    public int findPieceColour(int square, int Colour) {
        if ((BitBoards[GameConstants.KING + Colour] & GameConstants.SquareBits[square]) != 0)
            return GameConstants.KING + Colour;
        if ((BitBoards[GameConstants.ELEPHANT + Colour] & GameConstants.SquareBits[square]) != 0)
            return GameConstants.ELEPHANT + Colour;
        if ((BitBoards[GameConstants.KNIGHT + Colour] & GameConstants.SquareBits[square]) != 0)
            return GameConstants.KNIGHT + Colour;
        if ((BitBoards[GameConstants.BOAT + Colour] & GameConstants.SquareBits[square]) != 0)
            return GameConstants.BOAT + Colour;
        if ((BitBoards[GameConstants.PAWN + Colour] & GameConstants.SquareBits[square]) != 0)
            return GameConstants.PAWN + Colour;
        return GameConstants.EMPTY_SQUARE;
    }

    public int FindColourPieceInSquare(int square) {
        if ((BitBoards[GameConstants.ALL_RED_PIECES] & GameConstants.SquareBits[square]) != 0)
            return GameConstants.RED;
        if ((BitBoards[GameConstants.ALL_BLUE_PIECES] & GameConstants.SquareBits[square]) != 0)
            return GameConstants.BLUE;
        if ((BitBoards[GameConstants.ALL_GREEN_PIECES] & GameConstants.SquareBits[square]) != 0)
            return GameConstants.GREEN;
        if ((BitBoards[GameConstants.ALL_YELLOW_PIECES] & GameConstants.SquareBits[square]) != 0)
            return GameConstants.YELLOW;
        return -1;
    }

    public void NextPlayer() {
        CurrentPlayer = (CurrentPlayer + 1) % 4;
    }

    // Apply the move given and update the bit boards.
    public void ApplyMove(AIMove theMove) {
        // Check if the move is a promotion
        boolean isPromotion = (theMove.getPromoType() > 0);

        // Check if the piece moved was a pawn. If so, determine its promotion piece and update
        // the relevant board.

        if ((GameConstants.SquareBits[theMove.getSource()] & BitBoards[GameConstants.KNIGHT_PAWNS]) != 0) {
            RemovePiece(theMove.getSource(), GameConstants.KNIGHT_PAWNS);
            AddPiece(theMove.getDestination(), GameConstants.KNIGHT_PAWNS);
        } else if ((GameConstants.SquareBits[theMove.getSource()] & BitBoards[GameConstants.BOAT_PAWNS]) != 0) {
            RemovePiece(theMove.getSource(), GameConstants.BOAT_PAWNS);
            AddPiece(theMove.getDestination(), GameConstants.BOAT_PAWNS);
        } else if ((GameConstants.SquareBits[theMove.getSource()] & BitBoards[GameConstants.ELEPHANT_PAWNS]) != 0) {
            RemovePiece(theMove.getSource(), GameConstants.ELEPHANT_PAWNS);
            AddPiece(theMove.getDestination(), GameConstants.ELEPHANT_PAWNS);
        } else if ((GameConstants.SquareBits[theMove.getSource()] & BitBoards[GameConstants.KING_PAWNS]) != 0) {
            RemovePiece(theMove.getSource(), GameConstants.KING_PAWNS);
            AddPiece(theMove.getDestination(), GameConstants.KING_PAWNS);
        }

        switch (theMove.getType()) {
            case GameConstants.NORMAL_MOVE:
                RemovePiece(theMove.getSource(), theMove.getPiece());
                AddPiece(theMove.getDestination(), theMove.getPiece());
                break;
            case GameConstants.CAPTURE:
                RemovePiece(theMove.getSource(), theMove.getPiece());
                RemovePiece(theMove.getDestination(), theMove.getCaptured());
                AddPiece(theMove.getDestination(), theMove.getPiece());
                break;
            case GameConstants.RESIGN:
                break;
        }

        // Must delete the other players' boats!
        int boat_square;
        if (theMove.getTriumph()) {
            for (int i = 1; i < 4; i++) {
                boat_square = FindBoatSquare((CurrentPlayer + i) % 4);
                if (boat_square >= 0) {
                    RemovePiece(boat_square, GameConstants.BOAT + ((CurrentPlayer + i) % 4));
                } else
                    logger.error("ERROR CALCULATING BOAT TRIUMPH");
            }
        }

        // Handle the pawn promotions
        if (isPromotion) {
            int colour = (theMove.getPiece() % 4);
            switch (theMove.getPromoType()) {
                case GameConstants.KNIGHT:
                    if (BitBoards[GameConstants.KNIGHT + colour] == 0) {
                        RemovePiece(theMove.getDestination(), theMove.getPiece());
                        RemovePiece(theMove.getDestination(), GameConstants.KNIGHT_PAWNS);
                        AddPiece(theMove.getDestination(), GameConstants.KNIGHT + colour);
                    }
                    break;
                case GameConstants.BOAT:
                    if (BitBoards[GameConstants.BOAT + colour] == 0) {
                        RemovePiece(theMove.getDestination(), theMove.getPiece());
                        RemovePiece(theMove.getDestination(), GameConstants.BOAT_PAWNS);
                        AddPiece(theMove.getDestination(), GameConstants.BOAT + colour);
                    }
                    break;
                case GameConstants.ELEPHANT:
                    if (BitBoards[GameConstants.ELEPHANT + colour] == 0) {
                        RemovePiece(theMove.getDestination(), theMove.getPiece());
                        RemovePiece(theMove.getDestination(), GameConstants.ELEPHANT_PAWNS);
                        AddPiece(theMove.getDestination(), GameConstants.ELEPHANT + colour);
                    }
                    break;
                case GameConstants.KING:
                    if (BitBoards[GameConstants.KING + colour] == 0) {
                        RemovePiece(theMove.getDestination(), theMove.getPiece());
                        RemovePiece(theMove.getDestination(), GameConstants.KING_PAWNS);
                        AddPiece(theMove.getDestination(), GameConstants.KING + colour);
                    }
                    break;
            }
        }
        EvalMaterial();

        NextPlayer();
    }


    /*---- Helper functions ------*/

    // Add a piece (whichPiece) to the board at square (whichSquare).
    private boolean AddPiece(int whichSquare, int whichPiece) {
        // Add the piece to the corresponding bitboard
        BitBoards[whichPiece] |= GameConstants.SquareBits[whichSquare];

        // Now update the bitboard of all pieces of that particular colour.
        // Note that we check the value given is less than ALL_PIECES, this makes sure
        // that we are dealing with a specific piece with a specific colour.
        // The bitboards above this number deal with a number of colours at the same time
        // and so should not be factored into the updating of a particular side.

        if (whichPiece < GameConstants.ALL_PIECES) {
            BitBoards[GameConstants.ALL_PIECES + (whichPiece % 4)] |= GameConstants.SquareBits[whichSquare];
        }

        return true;
    }

    // Remove a piece from the board.
    private boolean RemovePiece(int whichSquare, int whichPiece) {
        // Remove the piece from the corresponding bitboard.
        BitBoards[whichPiece] ^= GameConstants.SquareBits[whichSquare];

        // Update the bitboard representing the player's colour.
        if (whichPiece < GameConstants.ALL_PIECES) {
            BitBoards[GameConstants.ALL_PIECES + (whichPiece % 4)] ^= GameConstants.SquareBits[whichSquare];
        }

        return true;
    }

    // Delete all pieces from the board
    private void EmptyBoard() {
        for (int i = 0; i < GameConstants.ALL_BITBOARDS; i++) {
            BitBoards[i] = 0;
        }
    }

    public void Print() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        for (int line = 0; line < 8; line++) {
            stringBuilder.append("  -----------------------------------------");
            stringBuilder.append("\n");
            stringBuilder.append("  |    |    |    |    |    |    |    |    |");
            stringBuilder.append("\n");
            stringBuilder.append(8 - line).append(" ");
            for (int col = 0; col < 8; col++) {
                long bits = GameConstants.SquareBits[line * 8 + col];

                // Scan the bitboards to find a piece, if any
                int piece = 0;
                while ((piece < GameConstants.ALL_PIECES) && ((bits & BitBoards[piece]) == 0))
                    piece++;

                // Show the piece
                stringBuilder.append("| ").append(GameConstants.PieceStrings[piece]).append(" ");
            }
            stringBuilder.append("|");
            stringBuilder.append("\n");
            stringBuilder.append("  |    |    |    |    |    |    |    |    |");
            stringBuilder.append("\n");
        }
        stringBuilder.append("  -----------------------------------------");
        stringBuilder.append("\n");
        stringBuilder.append("    A    B    C    D    E    F    G    H   ");
        stringBuilder.append("\n");

        logger.info(stringBuilder.toString());
        logger.debug(stringBuilder.toString());
    }

    public int FindBoatSquare(int colour) {
        for (int square = 0; square < 64; square++) {
            if ((GameConstants.SquareBits[square] & BitBoards[GameConstants.BOAT + colour]) != 0)
                return square;
        }
        return -1;
    }

}