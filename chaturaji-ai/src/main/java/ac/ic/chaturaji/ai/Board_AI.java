package ac.ic.chaturaji.ai;
// Chaturaji.Board_AI: constructs all relevant bitboards and handles chaturaji board manipulation (i.e. adding and removing pieces).
// The board is represented by a 64-bit string, the first bit denoting square a8, the second is a7 and so forth.
// There are 32 bitboards in total. The first 20 are for each piece of each player; for example Green will have 5 bitboards,
// one for each of its different pieces. The next 4 are used to denote the positions of ALL the individual players' pieces,
// essentially just the 5 single bitboards concatenated into one.
// The following 4 are the end squares opposite to each colour's starting position. These are the squares on which the pawns may
// promote to a higher value piece given that their corresponding piece has been taken.
// The final four are used to keep track of which pawns are potential boats/elephants/kings/knights upon promotion.

public class Board_AI {

    /*------ Data Members ------*/

    private int MaterialValue[ ];
		
	// The actual data representation of a chaturaji board.  First, an array of
	// bitboards, each of which contains flags for the squares where you can
	// find a specific type of piece
	private long BitBoards[];
		
	// Who is currently playing - 0 if yellow, 1 if blue etc.
	int CurrentPlayer;

	/*------ Methods ------*/

    /* Constructor */
    public Board_AI()
    {
        BitBoards = new long[ GameConstants_AI.ALL_BITBOARDS ];
        //NumPawns = new int[ 4 ];
        MaterialValue = new int[ 4 ];
        StartBoard();
    }
//Need to add the material value after adding it to the API
    public Board_AI(long bitboards[]){
        BitBoards = bitboards;
    }

    /* Accessors */
    public int GetCurrentPlayer()  { return CurrentPlayer; }

    public long GetBitBoard( int which ) { return BitBoards[ which ]; }
    public long[] GetBitBoards() { return BitBoards; }

    /* Functions */

    // Initialise the Board_AI:
    public boolean StartBoard()
    {
        // Empty the board of anything that may be on it.
        EmptyBoard();

        AddPiece(0, GameConstants_AI.YELLOW_BOAT);
        AddPiece(8, GameConstants_AI.YELLOW_KNIGHT);
        AddPiece(16, GameConstants_AI.YELLOW_ELEPHANT);
        AddPiece(24, GameConstants_AI.YELLOW_KING);

        AddPiece( 4, GameConstants_AI.BLUE_KING );
        AddPiece( 5, GameConstants_AI.BLUE_ELEPHANT );
        AddPiece( 6, GameConstants_AI.BLUE_KNIGHT );
        AddPiece( 7, GameConstants_AI.BLUE_BOAT);

        AddPiece(39, GameConstants_AI.RED_KING);
        AddPiece(47, GameConstants_AI.RED_ELEPHANT);
        AddPiece(55, GameConstants_AI.RED_KNIGHT);
        AddPiece(63, GameConstants_AI.RED_BOAT);

        AddPiece( 56, GameConstants_AI.GREEN_BOAT);
        AddPiece( 57, GameConstants_AI.GREEN_KNIGHT );
        AddPiece( 58, GameConstants_AI.GREEN_ELEPHANT );
        AddPiece( 59, GameConstants_AI.GREEN_KING);

        for (int i = 1; i <= 25; i = i + 8) {
            AddPiece(i, GameConstants_AI.YELLOW_PAWN);
        }
        for( int i = 12; i <= 15; i++ )
        {
            AddPiece(i, GameConstants_AI.BLUE_PAWN);
        }
        for (int i = 38; i <= 62; i = i + 8) {
            AddPiece(i, GameConstants_AI.RED_PAWN);
        }
        for( int i = 48; i <= 51; i++ )
        {
            AddPiece( i, GameConstants_AI.GREEN_PAWN);
        }

        for (int i = 7; i <= 63; i = i + 8) {
            BitBoards[GameConstants_AI.YELLOW_END_SQUARES] |= GameConstants_AI.SquareBits[i];
        }
        for (int i = 56; i <= 63; i++) {
            BitBoards[GameConstants_AI.BLUE_END_SQUARES] |= GameConstants_AI.SquareBits[i];
        }
        for (int i = 0; i <= 56; i = i + 8) {
            BitBoards[GameConstants_AI.RED_END_SQUARES] |= GameConstants_AI.SquareBits[i];
        }
        for (int i = 0; i <= 7; i++) {
            BitBoards[GameConstants_AI.GREEN_END_SQUARES] |= GameConstants_AI.SquareBits[i];
        }

        // These bitboards represent the different types of pawn promotion. For example, the bitboard BOAT_PAWNS contains all pawns that
        // may promote to a boat piece upon reaching their end squares.
        BitBoards[GameConstants_AI.BOAT_PAWNS] = (GameConstants_AI.SquareBits[1] | GameConstants_AI.SquareBits[15] | GameConstants_AI.SquareBits[48] | GameConstants_AI.SquareBits[62]);
        BitBoards[GameConstants_AI.KNIGHT_PAWNS] = (GameConstants_AI.SquareBits[9] | GameConstants_AI.SquareBits[14] | GameConstants_AI.SquareBits[49] | GameConstants_AI.SquareBits[54]);
        BitBoards[GameConstants_AI.ELEPHANT_PAWNS] = (GameConstants_AI.SquareBits[17] | GameConstants_AI.SquareBits[13] | GameConstants_AI.SquareBits[50] | GameConstants_AI.SquareBits[46]);
        BitBoards[GameConstants_AI.KING_PAWNS] = (GameConstants_AI.SquareBits[25] | GameConstants_AI.SquareBits[12] | GameConstants_AI.SquareBits[51] | GameConstants_AI.SquareBits[38]);

        // Player to go first is always yellow
        SetCurrentPlayer(GameConstants_AI.YELLOW);
        return true;
    }

	// Look for the piece located on a specific square
    public int FindPieceColour(int square, int Colour){
        if ( ( BitBoards[ GameConstants_AI.KING + Colour ] & GameConstants_AI.SquareBits[ square ] ) != 0 )
            return GameConstants_AI.KING + Colour;
        if ( ( BitBoards[ GameConstants_AI.ELEPHANT + Colour ] & GameConstants_AI.SquareBits[ square ] ) != 0 )
            return GameConstants_AI.ELEPHANT + Colour;
        if ( ( BitBoards[ GameConstants_AI.KNIGHT  + Colour] & GameConstants_AI.SquareBits[ square ] ) != 0 )
            return GameConstants_AI.KNIGHT + Colour;
        if ( ( BitBoards[ GameConstants_AI.BOAT + Colour ] & GameConstants_AI.SquareBits[ square ] ) != 0 )
            return GameConstants_AI.BOAT + Colour;
        if ( ( BitBoards[ GameConstants_AI.PAWN + Colour ] & GameConstants_AI.SquareBits[ square ] ) != 0 )
            return GameConstants_AI.PAWN + Colour;
        return GameConstants_AI.EMPTY_SQUARE;
    }

    // Returns the colour of the piece in a square or -1 if the square is empty
    public int FindColourPieceInSquare(int square){
        if( ( BitBoards[GameConstants_AI.RED] & square ) != 0)
            return GameConstants_AI.RED;
        if( ( BitBoards[GameConstants_AI.BLUE] & square )!= 0)
            return GameConstants_AI.BLUE;
        if( ( BitBoards[GameConstants_AI.GREEN] & square )!= 0)
            return GameConstants_AI.GREEN;
        if( ( BitBoards[GameConstants_AI.YELLOW] & square )!= 0)
            return GameConstants_AI.YELLOW;
        return -1;
    }

	  
	  private void SetCurrentPlayer( int which )
	  {
	    CurrentPlayer = which;
	  }
	  
	  // Change the identity of the player to move
	  public int SwitchSides()
	  {
	    if ( CurrentPlayer == GameConstants_AI.YELLOW)
	      SetCurrentPlayer(GameConstants_AI.BLUE);
	    else if (CurrentPlayer == GameConstants_AI.BLUE)
		  SetCurrentPlayer(GameConstants_AI.RED);
	    else if (CurrentPlayer == GameConstants_AI.RED)
		  SetCurrentPlayer(GameConstants_AI.GREEN);
	    else
	      SetCurrentPlayer(GameConstants_AI.YELLOW);
	    
	    return CurrentPlayer;
	  }

	  // Change the game board's representation to reflect the move
	  // received as a parameter
      // The type of the move is figured out in the Player class (if human) and the MoveGenerator class
      // if AI.
	  public void ApplyMove( Move_AI theMove )
	  {
	    // Check if the move is a promotion
	    boolean isPromotion = (theMove.getPromoType() > 0);

	    // Check if the piece moved was a pawn. If so, determine its promotion piece and update
	    // the relevant board.

	    if ((BitBoards[theMove.getPiece()] & BitBoards[GameConstants_AI.KNIGHT_PAWNS]) != 0) {
            RemovePiece(theMove.getSource(), GameConstants_AI.KNIGHT_PAWNS);
            AddPiece(theMove.getDest(), GameConstants_AI.KNIGHT_PAWNS);
	    }
	    else if ((BitBoards[theMove.getPiece()] & BitBoards[GameConstants_AI.BOAT_PAWNS]) != 0) {
            RemovePiece(theMove.getSource(), GameConstants_AI.BOAT_PAWNS);
            AddPiece(theMove.getDest(), GameConstants_AI.BOAT_PAWNS);
	    }
	    else if ((BitBoards[theMove.getPiece()] & BitBoards[GameConstants_AI.ELEPHANT_PAWNS]) != 0) {
            RemovePiece(theMove.getSource(), GameConstants_AI.ELEPHANT_PAWNS);
            AddPiece(theMove.getDest(), GameConstants_AI.ELEPHANT_PAWNS);
	    }
	    else if ((BitBoards[theMove.getPiece()] & BitBoards[GameConstants_AI.KING_PAWNS]) != 0) {
            RemovePiece(theMove.getSource(), GameConstants_AI.KING_PAWNS);
            AddPiece(theMove.getDest(), GameConstants_AI.KING_PAWNS);
	    }

	    switch(theMove.getType())
	    {
	      case GameConstants_AI.NORMAL_MOVE:
	        // The simple case
	        RemovePiece( theMove.getSource(), theMove.getPiece() );
	        AddPiece( theMove.getDest(), theMove.getPiece() );
	        break;
	      case GameConstants_AI.CAPTURE:
	        // Don't forget to remove the captured piece!
	        RemovePiece( theMove.getSource(), theMove.getPiece() );
	        RemovePiece( theMove.getDest(), theMove.getCaptured() );
	        AddPiece( theMove.getDest(), theMove.getPiece() );
	        break;
	      case GameConstants_AI.RESIGN:
	        break;
	    }

        int boat_square;
        if (theMove.getTriumph()) {
            for (int i = 1; i < 4; i++) {
                boat_square = FindBoatSquare((CurrentPlayer + i) % 4);
                if (boat_square >= 0) {
                    RemovePiece(boat_square, GameConstants_AI.BOAT + ((CurrentPlayer + i) % 4));
                }
                else
                    System.out.println("ERROR CALCULATING BOAT TRIUMPH");
            }
        }

	    // And now, apply the promotion
	    if (isPromotion) {
		int colour = ( theMove.getPiece() % 4);
		switch(theMove.getPromoType())
		    {
		    case GameConstants_AI.KNIGHT:
			if (BitBoards[GameConstants_AI.KNIGHT + colour] == 0) {
			    RemovePiece( theMove.getDest(), theMove.getPiece() );
			    RemovePiece( theMove.getDest(), GameConstants_AI.KNIGHT_PAWNS);
			    AddPiece( theMove.getDest(), GameConstants_AI.KNIGHT + colour );
			}
			break;
		    case GameConstants_AI.BOAT:
			if (BitBoards[GameConstants_AI.BOAT + colour] == 0) {
			    RemovePiece( theMove.getDest(), theMove.getPiece() );
			    RemovePiece( theMove.getDest(), GameConstants_AI.BOAT_PAWNS);
			    AddPiece( theMove.getDest(), GameConstants_AI.BOAT + colour );
			}
			break;
		    case GameConstants_AI.ELEPHANT:
			if (BitBoards[GameConstants_AI.ELEPHANT + colour] == 0) {
			    RemovePiece( theMove.getDest(), theMove.getPiece() );
			    RemovePiece( theMove.getDest(), GameConstants_AI.ELEPHANT_PAWNS);
			    AddPiece( theMove.getDest(), GameConstants_AI.ELEPHANT + colour );
			}
			break;
		    case GameConstants_AI.KING:
			if (BitBoards[GameConstants_AI.KING + colour] == 0) {
			    RemovePiece( theMove.getDest(), theMove.getPiece() );
			    RemovePiece( theMove.getDest(), GameConstants_AI.KING_PAWNS);
			    AddPiece( theMove.getDest(), GameConstants_AI.KING + colour );
			}
			break;
		    }
	    }
	    
	    SetCurrentPlayer( ( GetCurrentPlayer() + 1 ) % 4 );
	  }

    /******************************************************************************
     * PRIVATE METHODS
     *****************************************************************************/

    // private boolean AddPiece
    // Place a specific piece on a specific board square
    private boolean AddPiece( int whichSquare, int whichPiece )
    {
        // Add the piece itself
        BitBoards[ whichPiece ] |= GameConstants_AI.SquareBits[ whichSquare ];

        // And note the new piece position in the bitboard containing all
        // pieces of its color.  Here, we take advantage of the fact that
        // all pieces of a given color are represented by numbers of the same
        // parity

        if (whichPiece < GameConstants_AI.ALL_PIECES) {
            BitBoards[ GameConstants_AI.ALL_PIECES + ( whichPiece % 4 ) ] |= GameConstants_AI.SquareBits[ whichSquare ];

            // And adjust material balance accordingly
            MaterialValue[ whichPiece % 4 ] += GameConstants_AI.PieceValues[ whichPiece ];
        }

	   /* if ( whichPiece ==GameConstants_AI. YELLOW_PAWN )
	    	NumPawns[GameConstants_AI.YELLOW]++;
	    else if ( whichPiece == GameConstants_AI.BLUE_PAWN )
		NumPawns[GameConstants_AI.BLUE]++;
	    else if ( whichPiece ==GameConstants_AI. RED_PAWN )
		NumPawns[GameConstants_AI.RED]++;
	    else if ( whichPiece == GameConstants_AI.GREEN_PAWN )
		NumPawns[GameConstants_AI.GREEN]++;  */

        return true;
    }

    // private boolean RemovePiece
    // Eliminate a specific piece from a specific square on the board
    // Note that you MUST know that the piece is there before calling this,
    // or the results will not be what you expect!
    private boolean RemovePiece( int whichSquare, int whichPiece )
    {
        // Remove the piece itself
        BitBoards[ whichPiece ] ^= GameConstants_AI.SquareBits[ whichSquare ];

        if (whichPiece < GameConstants_AI.ALL_PIECES) {
            BitBoards[ GameConstants_AI.ALL_PIECES + ( whichPiece % 4 ) ] ^= GameConstants_AI.SquareBits[ whichSquare ];

            // And adjust material balance accordingly
            MaterialValue[ whichPiece % 4 ] -= GameConstants_AI.PieceValues[ whichPiece];
        }
       /* if ( whichPiece == YELLOW_PAWN )
          NumPawns[YELLOW]--;
        else if ( whichPiece == BLUE_PAWN )
          NumPawns[BLUE]--;
        else if ( whichPiece == RED_PAWN )
          NumPawns[RED]--;
        else if ( whichPiece == GREEN_PAWN )
          NumPawns[GREEN]--;
        return true;*/
        return true;
    }


    // private boolean EmptyBoard
    // Remove every piece from the board
    private boolean EmptyBoard()
    {
        for( int i = 0; i < GameConstants_AI.ALL_BITBOARDS; i++ )
        {
            BitBoards[ i ] = 0;
        }

        for (int i = 0; i < 4; i++) {
            MaterialValue[ i ] = 0;
            //NumPawns[ i ] = 0;
        }
        return true;
    }

    public boolean Print()
    {
        for( int line = 0; line < 8; line++ )
        {
            System.out.println( "-----------------------------------------" );
            System.out.println( "|    |    |    |    |    |    |    |    |" );
            for( int col = 0; col < 8; col++ )
            {
                long bits = GameConstants_AI.SquareBits[ line * 8 + col ];

                // Scan the bitboards to find a piece, if any
                int piece = 0;
                while ( ( piece < GameConstants_AI.ALL_PIECES ) && ( ( bits & BitBoards[ piece ] ) == 0 ) )
                    piece++;

                // Show the piece
                System.out.print( "| " + GameConstants_AI.PieceStrings[ piece ] + " " );
            }
            System.out.println( "|" );
            System.out.println( "|    |    |    |    |    |    |    |    |" );
        }
        System.out.println( "-----------------------------------------" );

        if (CurrentPlayer == GameConstants_AI.YELLOW)
            System.out.println( "Next to move is blue.");
        else if (CurrentPlayer == GameConstants_AI.BLUE)
            System.out.println( "Next to move is red." );
        else if (CurrentPlayer == GameConstants_AI.RED)
            System.out.println( "Next to move is green." );
        else
            System.out.println("Next to move is yellow.");

        return true;
    }

    public int FindBoatSquare(int colour) {
        for (int square = 0; square < 64; square++) {
            if ((GameConstants_AI.SquareBits[square] & BitBoards[GameConstants_AI.BOAT + colour]) != 0)
                return square;
        }
    return -1;
    }

}
