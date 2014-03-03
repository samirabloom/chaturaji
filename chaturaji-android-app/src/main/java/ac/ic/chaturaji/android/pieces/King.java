package ac.ic.chaturaji.android.pieces;

/**
 * Created by Kadir on March 2.
 */
public class King extends Pieces {

    public King(int colour) {
        this.colour = colour;
    }

    public boolean[][] valid_moves(int column, int row, Pieces[][] Board) {

        boolean[][] valid_moves = new boolean[8][8];

        if(column >= 1)
        {
            if(Board[column - 1][row] == null || this.colour != Board[column - 1][row].colour)
                valid_moves[column - 1][row] = true;
        }

        if(column <= 6)
        {
            if(Board[column + 1][row] == null || this.colour != Board[column + 1][row].colour)
                valid_moves[column + 1][row] = true;
        }

        if(row >= 1)
        {
            if(Board[column][row - 1] == null || this.colour != Board[column][row - 1].colour)
                valid_moves[column][row - 1] = true;
        }

        if(row <= 6)
        {
            if(Board[column][row + 1] == null || this.colour != Board[column][row + 1].colour)
                valid_moves[column][row + 1] = true;
        }

        if((column >= 1) && (row >= 1))
        {
            if(Board[column - 1][row - 1] == null || this.colour != Board[column - 1][row - 1].colour)
                valid_moves[column - 1][row - 1] = true;
        }

        if((column >= 1) && (row <= 6))
        {
            if(Board[column - 1][row + 1] == null || this.colour != Board[column - 1][row + 1].colour)
                valid_moves[column - 1][row + 1] = true;
        }

        if((column <= 6) && (row <= 6))
        {
            if(Board[column + 1][row + 1] == null || this.colour != Board[column + 1][row + 1].colour)
                valid_moves[column + 1][row + 1] = true;
        }

        if((column <= 6) && (row >= 1))
        {
            if(Board[column + 1][row - 1] == null || this.colour != Board[column + 1][row - 1].colour)
                valid_moves[column + 1][row - 1] = true;
        }
        
        return valid_moves;
    }
}
