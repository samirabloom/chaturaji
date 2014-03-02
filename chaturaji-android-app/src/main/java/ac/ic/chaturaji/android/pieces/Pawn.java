package ac.ic.chaturaji.android.pieces;

/**
 * Created by Kadir on March 2.
 */
public class Pawn extends Pieces {

    public Pawn(int colour) {
        this.colour = colour;
    }

    public boolean[][] valid_moves(int column, int row, Pieces[][] Board) {

        boolean[][] valid_moves = new boolean[8][8];

        if(Board[column][row].colour == 1 && (row <= 6))
        {
            if(Board[column][row + 1] == null)
                valid_moves[column][row + 1] = true;

            if(column <= 6)
                if(Board[column + 1][row + 1] != null && this.colour != Board[column + 1][row + 1].colour)
                    valid_moves[column + 1][row + 1] = true;

            if(column >= 1)
                if(Board[column - 1][row + 1] != null && this.colour != Board[column - 1][row + 1].colour)
                    valid_moves[column - 1][row + 1] = true;
        }

        if((Board[column][row].colour == 2) && (column <= 6))
        {
            if(Board[column + 1][row] == null)
                valid_moves[column + 1][row] = true;

            if(row <= 6)
                if(Board[column + 1][row + 1] != null && this.colour != Board[column + 1][row + 1].colour)
                    valid_moves[column + 1][row + 1] = true;

            if(row >= 1)
                if(Board[column + 1][row - 1] != null && this.colour != Board[column + 1][row - 1].colour)
                    valid_moves[column + 1][row - 1] = true;
        }

        if((Board[column][row].colour == 3) && (row >= 1))
        {
            if(Board[column][row - 1] == null)
                valid_moves[column][row - 1] = true;

            if(column <= 6)
                if(Board[column + 1][row - 1] != null && this.colour != Board[column + 1][row - 1].colour)
                    valid_moves[column + 1][row - 1] = true;

            if(column >= 1)
                if(Board[column - 1][row - 1] != null && this.colour != Board[column - 1][row - 1].colour)
                    valid_moves[column - 1][row - 1] = true;
        }

        if((Board[column][row].colour == 4) && (column >= 1))
        {
            if(Board[column - 1][row] == null)
                valid_moves[column - 1][row] = true;

            if(row <= 6)
                if(Board[column - 1][row + 1] != null && this.colour != Board[column - 1][row + 1].colour)
                    valid_moves[column - 1][row + 1] = true;

            if(row >= 1)
                if(Board[column - 1][row - 1] != null && this.colour != Board[column - 1][row - 1].colour)
                    valid_moves[column - 1][row - 1] = true;
        }

        return valid_moves;
    }
}
