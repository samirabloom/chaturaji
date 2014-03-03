package ac.ic.chaturaji.android.pieces;

/**
 * Created by Kadir on March 2.
 */
public class Knight extends Pieces {

    public Knight(int colour) {
        this.colour = colour;
    }

    public boolean[][] valid_moves(int column, int row, Pieces[][] Board) {
        
        boolean[][] valid_moves = new boolean[8][8];

        if((column >= 1) && (row <= 5))
        {
            if(Board[column - 1][row + 2] == null || this.colour != Board[column - 1][row + 2].colour)
                valid_moves[column - 1][row + 2] = true;
        }

        if((column <= 6) && (row <= 5))
        {
            if(Board[column + 1][row + 2] == null || this.colour != Board[column + 1][row + 2].colour)
                valid_moves[column + 1][row + 2] = true;
        }

        if((column >= 1) && (row >= 2))
        {
            if(Board[column - 1][row - 2] == null || this.colour != Board[column - 1][row - 2].colour)
                valid_moves[column - 1][row - 2] = true;
        }

        if((column <= 6) && (row >= 2))
        {
            if(Board[column + 1][row - 2] == null || this.colour != Board[column + 1][row - 2].colour)
                valid_moves[column + 1][row - 2] = true;
        }

        if((column >= 2) && (row >= 1))
        {
            if(Board[column - 2][row - 1] == null || this.colour != Board[column - 2][row - 1].colour)
                valid_moves[column - 2][row - 1] = true;
        }

        if((column <= 5) && (row >= 1))
        {
            if(Board[column + 2][row - 1] == null || this.colour != Board[column + 2][row - 1].colour)
                valid_moves[column + 2][row - 1] = true;
        }

        if((column >= 2) && (row <= 6))
        {
            if(Board[column - 2][row + 1] == null || this.colour != Board[column - 2][row + 1].colour)
                valid_moves[column - 2][row + 1] = true;
        }

        if((column <= 5) && (row <= 6))
        {
            if(Board[column + 2][row + 1] == null || this.colour != Board[column + 2][row + 1].colour)
                valid_moves[column + 2][row + 1] = true;
        }

        return valid_moves;
    }
}
