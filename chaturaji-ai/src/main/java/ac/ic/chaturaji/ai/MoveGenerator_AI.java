package ac.ic.chaturaji.ai;
//import Project.Chaturaji.*;

import java.util.*;

// MoveGenerator_AI contains methods used to generate all possible moves given a
// certain board position. It is implemented by the 'Movelist' class.

public class MoveGenerator_AI
{
    /*------ Data Members ------*/

    ArrayList<Move_AI> Moves;

    /*------ Methods ------*/

    // Constructor:
    public MoveGenerator_AI() {
        Moves = new ArrayList<Move_AI>();
    }

    // Accessors:
    public ArrayList GetMoves() {
        return Moves;
    }

    public int GetMoveSize() {
        return Moves.size();
    }

    // Functions:
    public boolean IsMove(Move_AI move) {

        for (Move_AI listMove: Moves)
            if (move.IsEqual(listMove))
                return true;

        // Cycled through list & move has not been found
        return false;
    }

    // Returns a valid move from the list
    public Move_AI FindMove(int source, int destination)
    {
        for (Move_AI listMove: Moves)
            if (listMove.getSource() == source && listMove.getDest() == destination)
                return listMove;

        return null;
    }

    public void Print()
    {
        for(Move_AI listMove: Moves)
        {
            listMove.Print();
        }
    }


    // Computes the possible moves for the given player:
    public void ComputeMoves(Board_AI board) {
        GenerateMoves(board, Moves, board.GetCurrentPlayer());
    }

    public void GenerateMoves(Board_AI board, ArrayList<Move_AI> Moves, int colour) {
	    Moves.clear();

	    GetKingMoves(board, Moves, colour);
	    GetElephantMoves(board, Moves, colour);
	    GetBoatMoves(board, Moves, colour);
	    GetKnightMoves(board, Moves, colour);
        GetPawnMoves(board, Moves, colour);
    }

    public void validateMove(Board_AI board, int colour, int Piece){
        switch(Piece){
            case(GameConstants_AI.KING):
                GetKingMoves(board, Moves, colour);
            case(GameConstants_AI.ELEPHANT):
                GetElephantMoves(board,Moves,colour);
            case(GameConstants_AI.BOAT):
                GetBoatMoves(board, Moves, colour);
            case(GameConstants_AI.KNIGHT):
                GetKnightMoves(board, Moves, colour);
            case(GameConstants_AI.PAWN):
                GetPawnMoves(board, Moves, colour);
        }

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

    public void DetermineMove(Board_AI board, int colour,  Move_AI move) {

        for (int j = 1; j < 4; j++) {
            if ((board.GetBitBoard(GameConstants_AI.ALL_PIECES + ((colour + j) % 4)) & GameConstants_AI.SquareBits[move.getDest()]) != 0)
            // If true then then we have located an enemy piece
            {
                move.SetType(GameConstants_AI.CAPTURE);
                move.SetCaptured(board.FindPieceColour(move.getDest(), colour + j));

                // Only one enemy piece may occupy the square, so return when the one found
                // has been processed.
                return;
            }


        }
        // If no enemy piece is found then it must be an empty square:
        move.SetType(GameConstants_AI.NORMAL_MOVE);
        move.SetCaptured(GameConstants_AI.EMPTY_SQUARE);
        //Check if the move is a boat triumph
        CheckBoatTriumph(board, move, colour, move.getSource(),move.getDest());

    }

    // Calculate the King's moves.
    private void GetKingMoves(Board_AI board, ArrayList<Move_AI> Moves, int colour)
    {
	    long kingBoard = board.GetBitBoard(GameConstants_AI.KING + colour);
	    int kSquare;
	    Move_AI newMove;
	    int destination;

	    // If there is no king then there is no need to generate moves.
	    if (kingBoard == 0)
	        return;

	    // Locate the King's square:
	    for (kSquare = 0; kSquare <= 63; kSquare++)
	        if ((GameConstants_AI.SquareBits[kSquare] & kingBoard) != 0)
		        break;

	    for (int i = 0; i < PieceMoves_AI.KingMoves[kSquare].length; i++) {
	        destination = PieceMoves_AI.KingMoves[kSquare][i];

	        if ((board.GetBitBoard(GameConstants_AI.ALL_PIECES + colour) & GameConstants_AI.SquareBits[destination]) == 0) {
		    // We have an empty square or an enemy piece at our destination!

		        newMove = new Move_AI(GameConstants_AI.KING + colour, kSquare, destination);

		        // Find out what type of move it is (capture or ordinary) and add it
		        // onto the end of the moves list:

		        DetermineMove(board, colour,  newMove);
		        Moves.add(newMove);
	         }
	    }
    }


    // Same as function above, but calculating the Boat moves.
    private void GetBoatMoves(Board_AI board, ArrayList<Move_AI> Moves, int colour)
    {
	    long boatBoard = board.GetBitBoard(GameConstants_AI.BOAT + colour);
	    int bSquare;
	    Move_AI newMove;
	    int destination;

	    if (boatBoard == 0)
	        return;

	    for (bSquare = 0; bSquare <= 63; bSquare++)
	        if ((GameConstants_AI.SquareBits[bSquare] & boatBoard) != 0)
		        break;

	    for (int i = 0; i < PieceMoves_AI.BoatMoves[bSquare].length; i++) {
	        destination = PieceMoves_AI.BoatMoves[bSquare][i];

	        if ((board.GetBitBoard(GameConstants_AI.ALL_PIECES + colour) & GameConstants_AI.SquareBits[destination]) == 0) {

		        newMove = new Move_AI(GameConstants_AI.BOAT + colour, bSquare, destination);
		        DetermineMove(board, colour, newMove);
                CheckBoatTriumph(board, newMove, colour, bSquare, destination);
		        Moves.add(newMove);
	        }
	    }
    }

    public void CheckBoatTriumph(Board_AI board, Move_AI newMove, int colour, int source, int destination)
    {
        // Use this to check whether the boats are in the 2x2 formation:
        long formationCheck = 771;
        long allBoats = 0;

        // Create a bit board representing all the boats currently in the game.
        for (int i = 1; i < 4; i++) {
            allBoats |= board.GetBitBoard(GameConstants_AI.BOAT + ((colour + i) % 4));
        }
        // Finally add the destination square:
        allBoats |= GameConstants_AI.SquareBits[destination];

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                // Check that we have an exact match:
                if ((formationCheck | allBoats) == (formationCheck & allBoats))  {
                    newMove.SetBoatTriumph(true);
                    return;
                }
                formationCheck <<= 1;
            }
            formationCheck <<= 2;
        }
    }

    // Calculate the Knight moves - same process as above.
    private void GetKnightMoves(Board_AI board, ArrayList<Move_AI> Moves, int colour)
    {
	    long knightBoard = board.GetBitBoard(GameConstants_AI.KNIGHT + colour);
	    int kSquare;
	    Move_AI newMove;
	    int destination;

	    if (knightBoard == 0)
	        return;

	    for (kSquare = 0; kSquare <= 63; kSquare++)
	        if ((GameConstants_AI.SquareBits[kSquare] & knightBoard) != 0)
		        break;

	    for (int i = 0; i < PieceMoves_AI.KnightMoves[kSquare].length; i++) {
	        destination = PieceMoves_AI.KnightMoves[kSquare][i];

	        if ((board.GetBitBoard(GameConstants_AI.ALL_PIECES + colour) & GameConstants_AI.SquareBits[destination]) == 0) {

		        newMove = new Move_AI(GameConstants_AI.KNIGHT + colour, kSquare, destination);
		        DetermineMove(board, colour, newMove);
		        Moves.add(newMove);
            }
	    }
    }


    // Calculate the elephant moves. Slightly more involved as the number of possible moves may
    // extend over a whole rank or file.
    private void GetElephantMoves(Board_AI board, ArrayList<Move_AI> Moves, int colour)
    {
	    long elephantBoard = board.GetBitBoard(GameConstants_AI.ELEPHANT + colour);
	    int eSquare;
	    Move_AI newMove;
	    int destination;

	    if (elephantBoard == 0)
	        return;

	    for (eSquare =0; eSquare <= 63; eSquare++)
	        if ((GameConstants_AI.SquareBits[eSquare] & elephantBoard) != 0)
		        break;

	    // elephantMoves may have a 3D array. The first entry corresponds to the square,
	    // the second to the direction of the moves it may take (up/down/left/right), and the
	    // third to moves it may go along the particular compass direction.

	    for (int direction = 0; direction < PieceMoves_AI.ElephantMoves[eSquare].length; direction++)
	        for (int j = 0; j < PieceMoves_AI.ElephantMoves[eSquare][direction].length; j++) {

		    destination = PieceMoves_AI.ElephantMoves[eSquare][direction][j];

		    // Check if our destination is occupied by a friendly piece. If so, move on to
		    // the next set of moves.
		    if ((board.GetBitBoard(GameConstants_AI.ALL_PIECES + colour) & GameConstants_AI.SquareBits[destination]) != 0)
		        break;

		    newMove = new Move_AI(GameConstants_AI.ELEPHANT + colour, eSquare, destination);
		    DetermineMove(board, colour, newMove);
		    Moves.add(newMove);
	        }
    }

    private void GetPawnMoves(Board_AI board, ArrayList<Move_AI> Moves, int colour)
    {
	    long pawnBoard = board.GetBitBoard(GameConstants_AI.PAWN + colour);
        int square;
        Move_AI newMove;
        int destination;
        int opp_colour;

        if (pawnBoard == 0)
            return;

        long allPieces = board.GetBitBoard(GameConstants_AI.ALL_YELLOW_PIECES) |
                board.GetBitBoard(GameConstants_AI.ALL_BLUE_PIECES) |
                board.GetBitBoard(GameConstants_AI.ALL_RED_PIECES) |
                board.GetBitBoard(GameConstants_AI.ALL_GREEN_PIECES);

         for (square = 0; square <= 63; square++) {
             // Find a pawn belonging to the current player's colour:
             if ((pawnBoard & GameConstants_AI.SquareBits[square]) != 0) {
                 // First test basic forward moves.
                 switch(colour) {
                     case 0: destination = square + 1; break;
                     case 1: destination = square + 8; break;
                     case 2: destination = square - 1; break;
                     default: destination = square - 8; break;
                 }

                 if ((allPieces & GameConstants_AI.SquareBits[destination]) == 0) {

                     if ((GameConstants_AI.SquareBits[destination] & board.GetBitBoard(GameConstants_AI.YELLOW_END_SQUARES + colour)) == 0) {
                         newMove = new Move_AI(GameConstants_AI.PAWN + colour, square, destination);
                         newMove.SetType(GameConstants_AI.NORMAL_MOVE);
                         newMove.SetCaptured(GameConstants_AI.EMPTY_SQUARE);
                         Moves.add(newMove);
                         CheckDoublePush(allPieces, square, destination, colour, Moves);
                     }
                     // If the above if statement is not true then we have a promotion.
                     else {
                         newMove = new Move_AI(GameConstants_AI.PAWN + colour, square, destination);
                         SetPromo(board, Moves, newMove, square);
                         newMove.SetType(GameConstants_AI.NORMAL_MOVE);
                         newMove.SetCaptured(GameConstants_AI.EMPTY_SQUARE);
                         Moves.add(newMove);
                     }
                 }

                 switch (colour) {
                 case 0: {
                     if (square < 8) {
                        destination = square + 9;
                        SetCapture(board, Moves, colour, square, destination);
                     }
                     else if (square >= 56) {
                         destination = square - 7;
                         SetCapture(board, Moves, colour, square, destination);
                     }
                     else {
                         destination = square + 9;
                         SetCapture(board, Moves, colour, square, destination);
                         destination = square - 7;
                         SetCapture(board, Moves, colour, square, destination);
                     }
                 }; break;
                 case 2: {
                     if (square < 8) {
                         destination = square + 7;
                         SetCapture(board, Moves, colour, square, destination);
                     }
                     else if (square >= 56) {
                         destination = square - 9;
                         SetCapture(board, Moves, colour, square, destination);
                     }
                     else {
                         destination = square + 7;
                         SetCapture(board, Moves, colour, square, destination);
                         destination = square - 9;
                         SetCapture(board, Moves, colour, square, destination);
                     }
                 }; break;
                 case 1: {
                     if ((square % 8) == 0) {
                         destination = square + 9;
                         SetCapture(board, Moves, colour, square, destination);
                     }
                     else if ((square % 8) == 7) {
                         destination = square + 7;
                         SetCapture(board, Moves, colour, square, destination);
                     }
                     else {
                         destination = square + 9;
                         SetCapture(board, Moves, colour, square, destination);
                         destination = square + 7;
                         SetCapture(board, Moves, colour, square, destination);
                     }
                 }; break;
                 case 3: {
                     if ((square % 8) == 0) {
                         destination = square - 7;
                         SetCapture(board, Moves, colour, square, destination);
                     }
                     else if ((square % 8) == 7) {
                         destination = square - 9;
                         SetCapture(board, Moves, colour, square, destination);
                     }
                     else {
                         destination = square - 7;
                         SetCapture(board, Moves, colour, square, destination);
                         destination = square - 9;
                         SetCapture(board, Moves, colour, square, destination);
                     }
                 }; break;
                 }
             }
          }
    }

    private void CheckDoublePush(long allPieces, int square, int destination, int colour, ArrayList<Move_AI> Moves)
    {
        Move_AI newMove;

        switch(colour) {
            case 0:
                if (square == 1 || square == 9 || square == 17 | square == 25) {
                    destination++;
                    if ((allPieces & GameConstants_AI.SquareBits[destination]) == 0) {
                        newMove = new Move_AI(GameConstants_AI.YELLOW_PAWN, square, destination);
                        newMove.SetType(GameConstants_AI.NORMAL_MOVE);
                        newMove.SetCaptured(GameConstants_AI.EMPTY_SQUARE);
			            Moves.add(newMove);
                    }
                }; break;
            case 1:
                if (square <= 15) {
                    destination += 8;
                    if ((allPieces & GameConstants_AI.SquareBits[destination]) == 0) {
                        newMove = new Move_AI(GameConstants_AI.BLUE_PAWN, square, destination);
                        newMove.SetType(GameConstants_AI.NORMAL_MOVE);
                        newMove.SetCaptured(GameConstants_AI.EMPTY_SQUARE);
			            Moves.add(newMove);
                    }
                }; break;
            case 2:
                if (square == 38 || square == 46 || square == 54 | square == 62) {
                    destination--;
                    if ((allPieces & GameConstants_AI.SquareBits[destination]) == 0) {
                        newMove = new Move_AI(GameConstants_AI.RED_PAWN, square, destination);
                        newMove.SetType(GameConstants_AI.NORMAL_MOVE);
                        newMove.SetCaptured(GameConstants_AI.EMPTY_SQUARE);
			            Moves.add(newMove);
                    }
                }; break;
            case 3:
                if (square >= 48) {
                    destination -= 8;
                    if ((allPieces & GameConstants_AI.SquareBits[destination]) == 0) {
                        newMove = new Move_AI(GameConstants_AI.GREEN_PAWN, square, destination);
                        newMove.SetType(GameConstants_AI.NORMAL_MOVE);
                        newMove.SetCaptured(GameConstants_AI.EMPTY_SQUARE);
			            Moves.add(newMove);
                    }
                }; break;
        }
    }

    private void SetPromo(Board_AI board, ArrayList<Move_AI> Moves, Move_AI newMove, int square)
    {
        if ((GameConstants_AI.SquareBits[square] & board.GetBitBoard(GameConstants_AI.KNIGHT_PAWNS)) != 0) {
            newMove.SetPromotion(GameConstants_AI.KNIGHT);
        }
        else if ((GameConstants_AI.SquareBits[square] & board.GetBitBoard(GameConstants_AI.BOAT_PAWNS)) != 0) {
            newMove.SetPromotion(GameConstants_AI.BOAT);
        }
        else if ((GameConstants_AI.SquareBits[square] & board.GetBitBoard(GameConstants_AI.ELEPHANT_PAWNS)) != 0) {
            newMove.SetPromotion(GameConstants_AI.ELEPHANT);
        }
        else {
            newMove.SetPromotion(GameConstants_AI.KING);
        }
    }

    private void SetCapture(Board_AI board, ArrayList<Move_AI> Moves, int colour, int source, int destination)
    {
        Move_AI newMove;
	int opp_colour;

        for (int i = 1; i < 4; i++) {
            if ((board.GetBitBoard(GameConstants_AI.ALL_PIECES + ((colour + i) % 4)) &
                    GameConstants_AI.SquareBits[destination]) != 0 ) {

                newMove = new Move_AI(GameConstants_AI.PAWN + colour, source, destination);
                newMove.SetType(GameConstants_AI.CAPTURE);
                opp_colour = (colour + i) % 4;

                if ((GameConstants_AI.SquareBits[destination] & board.GetBitBoard(GameConstants_AI.YELLOW_END_SQUARES + colour)) != 0)
                    SetPromo(board, Moves, newMove, source);

                newMove.SetCaptured(board.FindPieceColour(destination, opp_colour));
                Moves.add(newMove);
                return;
            }
        }
    }
}

