package ac.ic.chaturaji.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;

/**
 * MoveGenerator_AI contains methods used to generate all possible moves given a
 * certain board position. It is implemented by the 'Movelist' class.
 *
 * @author dg3213
 */
public class AIMoveGenerator {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /*------ Data Members ------*/

    ArrayList<AIMove> moves;
    ArrayList<AIMove> moves_2ply;

    /*------ Methods ------*/

    // Constructor

    public AIMoveGenerator() {
        moves = new ArrayList<>();
        moves_2ply = new ArrayList<>();
    }

    // Accessors

    public ArrayList GetMoves() {
        return moves;
    }

    public int GetMoveSize() {
        return moves.size();
    }

    // Functions:
    public boolean IsMove(AIMove move) {

        for (AIMove listMove : moves)
            if (move.IsEqual(listMove))
                return true;

        // Cycled through list & move has not been found
        return false;
    }

    // Returns a valid move from the list
    public AIMove findMove(int source, int destination) {
        for (AIMove listMove : moves) {
            if (listMove.getSource() == source && listMove.getDestination() == destination)
                return listMove;
        }

        return null;
    }

    public void print() {
        StringBuilder stringBuilder = new StringBuilder("The moves available to the current player are: \n");
        for (AIMove listMove : moves) {
            stringBuilder.append(listMove.Print()).append("\n");
        }
        logger.debug(stringBuilder.toString());
    }

    // Computes the possible moves for the given player:
    public ArrayList<AIMove> computeMoves(AIBoard board) {
        generateMoves(board, moves, board.getCurrentPlayer());
        Collections.sort(moves);
        return moves;
    }

    public void generateMoves(AIBoard board, ArrayList<AIMove> moves, int colour) {
        getKingMoves(board, moves, colour);
        getElephantMoves(board, moves, colour);
        getBoatMoves(board, moves, colour);
        getKnightMoves(board, moves, colour);
        getPawnMoves(board, moves, colour);
        //Collections.sort(moves);
    }


    /*------ GENERATING THE MOVES ------*/

    // The following functions (excluding the ones generating pawn moves) rely on the
    // data members contained below for quick move calculation. Within these data members
    // we have coded all possible moves of each type of piece for every square.
    // These functions merely look at the move array corresponding to a certain piece on a certain
    // square and cycle through its elements (corresponding to possible destinations), checking
    // to see if the destinations it contains are valid, and if so, what type of move they would
    // describe (i.e. normal move/ capture/ promotion).


    // Given a certain destination, figure out whether it is empty or occupied by a certain piece.
    // If occupied, return which colour's piece is located at the destination.

    private void determineMove(AIBoard board, int colour, int dest, AIMove move) {
        for (int j = 1; j < 4; j++) {
            if ((board.getBitBoard(GameConstants.ALL_PIECES + ((colour + j) % 4)) & GameConstants.SquareBits[dest]) != 0) {
                // If true then then we have located an enemy piece
                move.setType(GameConstants.CAPTURE);
                move.setCaptured(board.findPieceColour(dest, (colour + j) % 4));

                // only one enemy piece may occupy the square, so
                // return when the one found has been processed.
                return;
            }
        }
        // If no enemy piece is found then it must be an empty square:
        move.setType(GameConstants.NORMAL_MOVE);
        move.setCaptured(GameConstants.EMPTY_SQUARE);
    }

    // Calculate the King's moves.
    private void getKingMoves(AIBoard board, ArrayList<AIMove> moves, int colour) {
        long kingBoard = board.getBitBoard(GameConstants.KING + colour);
        int kSquare;
        AIMove newMove;
        int destination;

        // If there is no king then there is no need to generate moves.
        if (kingBoard == 0)
            return;

        // Locate the King's square:
        for (kSquare = 0; kSquare <= 63; kSquare++)
            if ((GameConstants.SquareBits[kSquare] & kingBoard) != 0)
                break;

        for (int i = 0; i < PieceMoves.KingMoves[kSquare].length; i++) {
            destination = PieceMoves.KingMoves[kSquare][i];

            if ((board.getBitBoard(GameConstants.ALL_PIECES + colour) & GameConstants.SquareBits[destination]) == 0) {
                // We have an empty square or an enemy piece at our destination!

                newMove = new AIMove(GameConstants.KING + colour, kSquare, destination);

                // Find out what type of move it is (capture or ordinary) and add it
                // onto the end of the moves list:

                determineMove(board, colour, destination, newMove);
                moves.add(newMove);
            }
        }
    }


    // Same as function above, but calculating the Boat moves.
    private void getBoatMoves(AIBoard board, ArrayList<AIMove> moves, int colour) {
        long boatBoard = board.getBitBoard(GameConstants.BOAT + colour);
        int bSquare;
        AIMove newMove;
        int destination;

        if (boatBoard == 0)
            return;

        for (bSquare = 0; bSquare <= 63; bSquare++)
            if ((GameConstants.SquareBits[bSquare] & boatBoard) != 0)
                break;

        for (int i = 0; i < PieceMoves.BoatMoves[bSquare].length; i++) {
            destination = PieceMoves.BoatMoves[bSquare][i];

            if ((board.getBitBoard(GameConstants.ALL_PIECES + colour) & GameConstants.SquareBits[destination]) == 0) {

                newMove = new AIMove(GameConstants.BOAT + colour, bSquare, destination);
                determineMove(board, colour, destination, newMove);
                CheckBoatTriumph(board, newMove, colour, bSquare, destination);
                moves.add(newMove);
            }
        }
    }

    private void CheckBoatTriumph(AIBoard board, AIMove newMove, int colour, int source, int destination) {
        // Use this to check whether the boats are in the 2x2 formation:
        long formationCheck = 771;
        long allBoats = 0;

        // Create a bit board representing all the boats currently in the game.
        for (int i = 1; i < 4; i++) {
            allBoats |= board.getBitBoard(GameConstants.BOAT + ((colour + i) % 4));
        }
        // Finally add the destination square:
        allBoats |= GameConstants.SquareBits[destination];

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                // Check that we have an exact match:
                if ((formationCheck | allBoats) == (formationCheck & allBoats)) {
                    newMove.setBoatTriumph(true);
                    return;
                }
                formationCheck <<= 1;
            }
            formationCheck <<= 2;
        }
    }

    // Calculate the Knight moves - same process as above.
    private void getKnightMoves(AIBoard board, ArrayList<AIMove> moves, int colour) {
        long knightBoard = board.getBitBoard(GameConstants.KNIGHT + colour);
        int kSquare;
        AIMove newMove;
        int destination;

        if (knightBoard == 0)
            return;

        for (kSquare = 0; kSquare <= 63; kSquare++)
            if ((GameConstants.SquareBits[kSquare] & knightBoard) != 0)
                break;

        for (int i = 0; i < PieceMoves.KnightMoves[kSquare].length; i++) {
            destination = PieceMoves.KnightMoves[kSquare][i];

            if ((board.getBitBoard(GameConstants.ALL_PIECES + colour) & GameConstants.SquareBits[destination]) == 0) {

                newMove = new AIMove(GameConstants.KNIGHT + colour, kSquare, destination);
                determineMove(board, colour, destination, newMove);
                moves.add(newMove);
            }
        }
    }


    private void getElephantMoves(AIBoard board, ArrayList<AIMove> moves, int colour) {
        long elephantBoard = board.getBitBoard(GameConstants.ELEPHANT + colour);

        int eSquare;
        AIMove newMove;
        int destination;

        if (elephantBoard == 0)
            return;

        for (eSquare = 0; eSquare <= 63; eSquare++)
            if ((GameConstants.SquareBits[eSquare] & elephantBoard) != 0)
                break;

        // elephantMoves may have a 3D array. The first entry corresponds to the square,
        // the second to the direction of the moves it may take (up/down/left/right), and the
        // third to moves it may go along the particular compass direction.

        for (int direction = 0; direction < PieceMoves.ElephantMoves[eSquare].length; direction++)
            for (int j = 0; j < PieceMoves.ElephantMoves[eSquare][direction].length; j++) {

                destination = PieceMoves.ElephantMoves[eSquare][direction][j];

                // Check if our destination is occupied by a friendly piece. If so, move on to
                // the next set of moves.
                if ((board.getBitBoard(GameConstants.ALL_PIECES + colour) & GameConstants.SquareBits[destination]) != 0)
                    break;

                newMove = new AIMove(GameConstants.ELEPHANT + colour, eSquare, destination);
                determineMove(board, colour, destination, newMove);
                moves.add(newMove);

                // Check if the move added was a capture - if so, we cannot continue along this line of attack!
                if (newMove.getCaptured() != GameConstants.EMPTY_SQUARE)
                    break;
            }
    }

    private void getPawnMoves(AIBoard board, ArrayList<AIMove> Moves, int colour) {
        long pawnBoard = board.getBitBoard(GameConstants.PAWN + colour);
        int square;
        AIMove newMove;
        int destination;

        if (pawnBoard == 0)
            return;

        long allPieces = board.getBitBoard(GameConstants.ALL_YELLOW_PIECES) |
                board.getBitBoard(GameConstants.ALL_BLUE_PIECES) |
                board.getBitBoard(GameConstants.ALL_RED_PIECES) |
                board.getBitBoard(GameConstants.ALL_GREEN_PIECES);

        for (square = 0; square <= 63; square++) {
            // Find a pawn belonging to the current player's colour:
            if ((pawnBoard & GameConstants.SquareBits[square]) != 0) {
                // First test basic forward moves.
                switch (colour) {
                    case 0:
                        destination = square + 1;
                        break;
                    case 1:
                        destination = square + 8;
                        break;
                    case 2:
                        destination = square - 1;
                        break;
                    default:
                        destination = square - 8;
                        break;
                }

                if (destination < 0 || destination >= 64)
                    continue;

                if ((allPieces & GameConstants.SquareBits[destination]) == 0) {

                    if ((GameConstants.SquareBits[destination] & board.getBitBoard(GameConstants.YELLOW_END_SQUARES + colour)) == 0) {
                        newMove = new AIMove(GameConstants.PAWN + colour, square, destination);
                        newMove.setType(GameConstants.NORMAL_MOVE);
                        newMove.setCaptured(GameConstants.EMPTY_SQUARE);
                        Moves.add(newMove);
                    }
                    // If the above if statement is true then we have a promotion.
                    else {
                        newMove = new AIMove(GameConstants.PAWN + colour, square, destination);
                        SetPromo(board, Moves, newMove, square);
                        newMove.setType(GameConstants.NORMAL_MOVE);
                        newMove.setCaptured(GameConstants.EMPTY_SQUARE);
                        Moves.add(newMove);
                    }
                }

                switch (colour) {
                    case 0: {
                        if (square < 8) {
                            destination = square + 9;
                            SetCapture(board, Moves, colour, square, destination);
                        } else if (square >= 56) {
                            destination = square - 7;
                            SetCapture(board, Moves, colour, square, destination);
                        } else {
                            destination = square + 9;
                            SetCapture(board, Moves, colour, square, destination);
                            destination = square - 7;
                            SetCapture(board, Moves, colour, square, destination);
                        }
                    }
                    break;
                    case 2: {
                        if (square < 8) {
                            destination = square + 7;
                            SetCapture(board, Moves, colour, square, destination);
                        } else if (square >= 56) {
                            destination = square - 9;
                            SetCapture(board, Moves, colour, square, destination);
                        } else {
                            destination = square + 7;
                            SetCapture(board, Moves, colour, square, destination);
                            destination = square - 9;
                            SetCapture(board, Moves, colour, square, destination);
                        }
                    }
                    break;
                    case 1: {
                        if ((square % 8) == 0) {
                            destination = square + 9;
                            SetCapture(board, Moves, colour, square, destination);
                        } else if ((square % 8) == 7) {
                            destination = square + 7;
                            SetCapture(board, Moves, colour, square, destination);
                        } else {
                            destination = square + 9;
                            SetCapture(board, Moves, colour, square, destination);
                            destination = square + 7;
                            SetCapture(board, Moves, colour, square, destination);
                        }
                    }
                    break;
                    case 3: {
                        if ((square % 8) == 0) {
                            destination = square - 7;
                            SetCapture(board, Moves, colour, square, destination);
                        } else if ((square % 8) == 7) {
                            destination = square - 9;
                            SetCapture(board, Moves, colour, square, destination);
                        } else {
                            destination = square - 7;
                            SetCapture(board, Moves, colour, square, destination);
                            destination = square - 9;
                            SetCapture(board, Moves, colour, square, destination);
                        }
                    }
                    break;
                }
            }
        }
    }

    private void SetPromo(AIBoard board, ArrayList<AIMove> Moves, AIMove newMove, int square) {
        if ((GameConstants.SquareBits[square] & board.getBitBoard(GameConstants.KNIGHT_PAWNS)) != 0) {
            newMove.setPromotion(GameConstants.KNIGHT);
        } else if ((GameConstants.SquareBits[square] & board.getBitBoard(GameConstants.BOAT_PAWNS)) != 0) {
            newMove.setPromotion(GameConstants.BOAT);
        } else if ((GameConstants.SquareBits[square] & board.getBitBoard(GameConstants.ELEPHANT_PAWNS)) != 0) {
            newMove.setPromotion(GameConstants.ELEPHANT);
        } else {
            newMove.setPromotion(GameConstants.KING);
        }
    }

    private void SetCapture(AIBoard board, ArrayList<AIMove> Moves, int colour, int source, int destination) {
        AIMove newMove;
        int opp_colour;

        for (int i = 1; i < 4; i++) {
            if ((board.getBitBoard(GameConstants.ALL_PIECES + ((colour + i) % 4)) &
                    GameConstants.SquareBits[destination]) != 0) {

                newMove = new AIMove(GameConstants.PAWN + colour, source, destination);
                newMove.setType(GameConstants.CAPTURE);
                opp_colour = (colour + i) % 4;

                if ((GameConstants.SquareBits[destination] & board.getBitBoard(GameConstants.YELLOW_END_SQUARES + colour)) != 0)
                    SetPromo(board, Moves, newMove, source);

                newMove.setCaptured(board.findPieceColour(destination, opp_colour));
                Moves.add(newMove);
                return;
            }
        }
    }
}