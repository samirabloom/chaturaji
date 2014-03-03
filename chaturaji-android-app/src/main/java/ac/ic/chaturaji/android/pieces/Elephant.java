package ac.ic.chaturaji.android.pieces;

/**
 * Created by Kadir on March 2.
 */
public class Elephant extends Pieces {

    public Elephant(int colour) {
        this.colour = colour;
    }
    
    public boolean[][] valid_moves(int column, int row, Pieces[][] Board) {
        
        boolean[][] valid_moves = new boolean[8][8];

        for(int i = 1; i <= (7 - column); i++)
        {
            if(Board[column + i][row] != null && this.colour == Board[column + i][row].colour)
                break;

            valid_moves[column + i][row] = true;

            if(Board[column + i][row] != null)
                break;
        }

        for(int i = 1; i <= column; i++)
        {
            if(Board[column - i][row] != null && this.colour == Board[column - i][row].colour)
                break;

            valid_moves[column - i][row] = true;

            if(Board[column - i][row] != null)
                break;
        }

        for(int i = 1; i <= (7 - row); i++)
        {
            if(Board[column][row + 1] != null && this.colour == Board[column][row + i].colour)
                break;

            valid_moves[column][row + i] = true;

            if(Board[column][row + i] != null)
                break;
        }

        for(int i = 1; i <= row; i++)
        {
            if(Board[column][row - i] != null && this.colour == Board[column][row - i].colour)
                break;

            valid_moves[column][row - i] = true;

            if(Board[column][row - i] != null)
                break;
        }

        return valid_moves;
    }
}
