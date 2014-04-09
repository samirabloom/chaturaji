package ac.ic.chaturaji.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * MoveGenerator_AI contains methods used to generate all possible moves given a
 * certain board position. It is implemented by the 'Movelist' class.
 *
 * @author dg3213
 */
public class MoveGenerator_AI {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    /*------ Data Members ------*/

    ArrayList<Move_AI> Moves;
    ArrayList<Move_AI> Moves_2ply;

    /*------ Methods ------*/

    // Constructor

    public MoveGenerator_AI() {
        Moves = new ArrayList<Move_AI>();
        Moves_2ply = new ArrayList<Move_AI>();
    }

    // Accessors

    public ArrayList GetMoves() {
        return Moves;
    }

    public int GetMoveSize() {
        return Moves.size();
    }

    // Functions:
    public boolean IsMove(Move_AI move) {

        for (Move_AI listMove : Moves)
            if (move.IsEqual(listMove))
                return true;

        // Cycled through list & move has not been found
        return false;
    }

    // Returns a valid move from the list
    public Move_AI FindMove(int source, int destination) {
        for (Move_AI listMove : Moves) {
            if (listMove.getSource() == source && listMove.getDest() == destination)
                return listMove;
        }

        return null;
    }

    public void Print() {
        StringBuilder stringBuilder = new StringBuilder("The Moves available to the current player are: \n");
        for (Move_AI listMove : Moves) {
            stringBuilder.append(listMove.Print()).append("\n");
        }
        logger.info(stringBuilder.toString());
    }

    /*
        public Board_AI[] Search_ply1(Board_AI board){

            ComputeMoves(board, Moves, false);
            Board_AI[] boards = new Board_AI[Moves.size()];
            for(int i = 0; i < Moves.size(); i++){
                boards[i] = board.clone();
                boards[i].ApplyMove(Moves.get(i));
            }
            return boards;
        }

        public int Search_ply2(Board_AI board, Board_AI[] boards){

            int CurrentPlayer = board.GetCurrentPlayer();
            int n = 0;
            int[] descendents = new int[boards.length];
            int sumOfDescendents = 0;
            for(int i = 0; i < boards.length; i++){
                ComputeMoves(boards[i], Moves_2ply,true);
                descendents[i] = Moves_2ply.size()-sumOfDescendents;
                sumOfDescendents += descendents[i];
            }

            Board_AI[] boards_ply2 = new Board_AI[Moves_2ply.size()];
            sumOfDescendents = 0;
            int[] MoveValue = new int[Moves_2ply.size()];
            for(int i = 0; i < Moves_2ply.size(); i++){
                if(i == sumOfDescendents + descendents[n]){
                    sumOfDescendents+=descendents[n];
                    n++;
                }
                boards_ply2[i] = boards[n].clone();
                boards_ply2[i].ApplyMove(Moves_2ply.get(i));
                MoveValue[i] = EvalMove(boards_ply2[i], board);
            }
            int maxEval = MoveValue[0];
            int maxIndex = 0;
            for(int i = 1; i < boards_ply2.length; i++){
                if(MoveValue[i] > maxEval){
                    maxEval = MoveValue[i];
                    maxIndex = i;
                }
            }
            n = 0;
            maxIndex = maxIndex - descendents[n]+1;
            while( maxIndex > 0){
                n++;
                maxIndex = maxIndex - descendents[n];
            }
            return n;
        }

        protected int EvalMove( Board_AI board, Board_AI originalBoard){
            int CurrentPlayer = board.GetCurrentPlayer();
            int[] MaterialValueBefore = originalBoard.GetMaterialValue();
            int[] MaterialValueAfter = board.GetMaterialValue();

            int CurrentPlayerValueBefore = 0;
            int CurrentPlayerValueAfter = 0;

            for(int i = 0; i < 4; i++){
                if( i == CurrentPlayer ){
                    CurrentPlayerValueBefore += MaterialValueBefore[i];
                    CurrentPlayerValueAfter += MaterialValueAfter[i];
                }
                else{
                    CurrentPlayerValueBefore -= MaterialValueBefore[i];
                    CurrentPlayerValueAfter -= MaterialValueAfter[i];
                }
            }
            return CurrentPlayerValueAfter-CurrentPlayerValueBefore;
        }

        public Move_AI Search(Board_AI board){

            int CurrentPlayer = board.GetCurrentPlayer();

            Board_AI[] boards = Search_ply1(board);
            Board_AI[] boards_ply2 ;
            int moveIndex = Search_ply2(board, boards);

            return Moves.get(moveIndex);

        }
    */
    // Computes the possible moves for the given player:
    public ArrayList<Move_AI> ComputeMoves(Board_AI board) {
        GenerateMoves(board, Moves, board.GetCurrentPlayer());
        return Moves;
    }

    public void GenerateMoves(Board_AI board, ArrayList<Move_AI> Moves, int colour) {

        GetKingMoves(board, Moves, colour);
        GetElephantMoves(board, Moves, colour);
        GetBoatMoves(board, Moves, colour);
        GetKnightMoves(board, Moves, colour);
        GetPawnMoves(board, Moves, colour);
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

    private void DetermineMove(Board_AI board, int colour, int dest, Move_AI move) {

        for (int j = 1; j < 4; j++) {
            if ((board.GetBitBoard(GameConstants.ALL_PIECES + ((colour + j) % 4)) & GameConstants.SquareBits[dest]) != 0)
            // If true then then we have located an enemy piece
            {
                move.SetType(GameConstants.CAPTURE);
                move.SetCaptured(board.FindPieceColour(dest, (colour + j) % 4));

                // Only one enemy piece may occupy the square, so return when the one found
                // has been processed.
                return;
            }

        }
        // If no enemy piece is found then it must be an empty square:
        move.SetType(GameConstants.NORMAL_MOVE);
        move.SetCaptured(GameConstants.EMPTY_SQUARE);
    }

    // Calculate the King's moves.
    private void GetKingMoves(Board_AI board, ArrayList<Move_AI> Moves, int colour) {
        long kingBoard = board.GetBitBoard(GameConstants.KING + colour);
        int kSquare;
        Move_AI newMove;
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

            if ((board.GetBitBoard(GameConstants.ALL_PIECES + colour) & GameConstants.SquareBits[destination]) == 0) {
                // We have an empty square or an enemy piece at our destination!

                newMove = new Move_AI(GameConstants.KING + colour, kSquare, destination);

                // Find out what type of move it is (capture or ordinary) and add it
                // onto the end of the moves list:

                DetermineMove(board, colour, destination, newMove);
                Moves.add(newMove);
            }
        }
    }


    // Same as function above, but calculating the Boat moves.
    private void GetBoatMoves(Board_AI board, ArrayList<Move_AI> Moves, int colour) {
        long boatBoard = board.GetBitBoard(GameConstants.BOAT + colour);
        int bSquare;
        Move_AI newMove;
        int destination;

        if (boatBoard == 0)
            return;

        for (bSquare = 0; bSquare <= 63; bSquare++)
            if ((GameConstants.SquareBits[bSquare] & boatBoard) != 0)
                break;

        for (int i = 0; i < PieceMoves.BoatMoves[bSquare].length; i++) {
            destination = PieceMoves.BoatMoves[bSquare][i];

            if ((board.GetBitBoard(GameConstants.ALL_PIECES + colour) & GameConstants.SquareBits[destination]) == 0) {

                newMove = new Move_AI(GameConstants.BOAT + colour, bSquare, destination);
                DetermineMove(board, colour, destination, newMove);
                CheckBoatTriumph(board, newMove, colour, bSquare, destination);
                Moves.add(newMove);
            }
        }
    }

    private void CheckBoatTriumph(Board_AI board, Move_AI newMove, int colour, int source, int destination) {
        // Use this to check whether the boats are in the 2x2 formation:
        long formationCheck = 771;
        long allBoats = 0;

        // Create a bit board representing all the boats currently in the game.
        for (int i = 1; i < 4; i++) {
            allBoats |= board.GetBitBoard(GameConstants.BOAT + ((colour + i) % 4));
        }
        // Finally add the destination square:
        allBoats |= GameConstants.SquareBits[destination];

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                // Check that we have an exact match:
                if ((formationCheck | allBoats) == (formationCheck & allBoats)) {
                    newMove.SetBoatTriumph(true);
                    return;
                }
                formationCheck <<= 1;
            }
            formationCheck <<= 2;
        }
    }

    // Calculate the Knight moves - same process as above.
    private void GetKnightMoves(Board_AI board, ArrayList<Move_AI> Moves, int colour) {
        long knightBoard = board.GetBitBoard(GameConstants.KNIGHT + colour);
        int kSquare;
        Move_AI newMove;
        int destination;

        if (knightBoard == 0)
            return;

        for (kSquare = 0; kSquare <= 63; kSquare++)
            if ((GameConstants.SquareBits[kSquare] & knightBoard) != 0)
                break;

        for (int i = 0; i < PieceMoves.KnightMoves[kSquare].length; i++) {
            destination = PieceMoves.KnightMoves[kSquare][i];

            if ((board.GetBitBoard(GameConstants.ALL_PIECES + colour) & GameConstants.SquareBits[destination]) == 0) {

                newMove = new Move_AI(GameConstants.KNIGHT + colour, kSquare, destination);
                DetermineMove(board, colour, destination, newMove);
                Moves.add(newMove);
            }
        }
    }


    private void GetElephantMoves(Board_AI board, ArrayList<Move_AI> Moves, int colour) {
        long elephantBoard = board.GetBitBoard(GameConstants.ELEPHANT + colour);

        int eSquare;
        Move_AI newMove;
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
                if ((board.GetBitBoard(GameConstants.ALL_PIECES + colour) & GameConstants.SquareBits[destination]) != 0)
                    break;

                newMove = new Move_AI(GameConstants.ELEPHANT + colour, eSquare, destination);
                DetermineMove(board, colour, destination, newMove);
                Moves.add(newMove);

                // Check if the move added was a capture - if so, we cannot continue along this line of attack!
                if (newMove.getCaptured() != GameConstants.EMPTY_SQUARE)
                    break;
            }
    }

    private void GetPawnMoves(Board_AI board, ArrayList<Move_AI> Moves, int colour) {
        long pawnBoard = board.GetBitBoard(GameConstants.PAWN + colour);
        int square;
        Move_AI newMove;
        int destination;

        if (pawnBoard == 0)
            return;

        long allPieces = board.GetBitBoard(GameConstants.ALL_YELLOW_PIECES) |
                board.GetBitBoard(GameConstants.ALL_BLUE_PIECES) |
                board.GetBitBoard(GameConstants.ALL_RED_PIECES) |
                board.GetBitBoard(GameConstants.ALL_GREEN_PIECES);

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

                    if ((GameConstants.SquareBits[destination] & board.GetBitBoard(GameConstants.YELLOW_END_SQUARES + colour)) == 0) {
                        newMove = new Move_AI(GameConstants.PAWN + colour, square, destination);
                        newMove.SetType(GameConstants.NORMAL_MOVE);
                        newMove.SetCaptured(GameConstants.EMPTY_SQUARE);
                        Moves.add(newMove);
                    }
                    // If the above if statement is true then we have a promotion.
                    else {
                        newMove = new Move_AI(GameConstants.PAWN + colour, square, destination);
                        SetPromo(board, Moves, newMove, square);
                        newMove.SetType(GameConstants.NORMAL_MOVE);
                        newMove.SetCaptured(GameConstants.EMPTY_SQUARE);
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

    private void SetPromo(Board_AI board, ArrayList<Move_AI> Moves, Move_AI newMove, int square) {
        if ((GameConstants.SquareBits[square] & board.GetBitBoard(GameConstants.KNIGHT_PAWNS)) != 0) {
            newMove.SetPromotion(GameConstants.KNIGHT);
        } else if ((GameConstants.SquareBits[square] & board.GetBitBoard(GameConstants.BOAT_PAWNS)) != 0) {
            newMove.SetPromotion(GameConstants.BOAT);
        } else if ((GameConstants.SquareBits[square] & board.GetBitBoard(GameConstants.ELEPHANT_PAWNS)) != 0) {
            newMove.SetPromotion(GameConstants.ELEPHANT);
        } else {
            newMove.SetPromotion(GameConstants.KING);
        }
    }

    private void SetCapture(Board_AI board, ArrayList<Move_AI> Moves, int colour, int source, int destination) {
        Move_AI newMove;
        int opp_colour;

        for (int i = 1; i < 4; i++) {
            if ((board.GetBitBoard(GameConstants.ALL_PIECES + ((colour + i) % 4)) &
                    GameConstants.SquareBits[destination]) != 0) {

                newMove = new Move_AI(GameConstants.PAWN + colour, source, destination);
                newMove.SetType(GameConstants.CAPTURE);
                opp_colour = (colour + i) % 4;

                if ((GameConstants.SquareBits[destination] & board.GetBitBoard(GameConstants.YELLOW_END_SQUARES + colour)) != 0)
                    SetPromo(board, Moves, newMove, source);

                newMove.SetCaptured(board.FindPieceColour(destination, opp_colour));
                Moves.add(newMove);
                return;
            }
        }
    }
}