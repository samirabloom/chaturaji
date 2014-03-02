package ac.ic.chaturaji.android.pieces;

/**
 * Created by Kadir on March 2.
 */
public class Boat extends Pieces {

    public Boat(int colour) {
        this.colour = colour;
    }
    
    public boolean[][] valid_moves(int column, int row, Pieces[][] Board) {

        boolean[][] valid_moves = new boolean[8][8];

        if((column <= 5) && (row <= 5))
        {
            if(Board[column + 2][row + 2] == null || this.colour != Board[column + 2][row + 2].colour)
                valid_moves[column + 2][row + 2] = true;
        }

        if((column >= 2) && (row >= 2))
        {
            if(Board[column - 2][row - 2] == null || this.colour != Board[column - 2][row - 2].colour)
                valid_moves[column - 2][row - 2] = true;
        }

        if((column >= 2) && (row <= 5))
        {
            if(Board[column - 2][row + 2] == null || this.colour != Board[column - 2][row + 2].colour)
                valid_moves[column - 2][row + 2] = true;
        }

        if((column <= 5) && (row >= 2))
        {
            if(Board[column + 2][row - 2] == null || this.colour != Board[column + 2][row - 2].colour)
                valid_moves[column + 2][row - 2] = true;
        }

        return valid_moves;
    }
}
