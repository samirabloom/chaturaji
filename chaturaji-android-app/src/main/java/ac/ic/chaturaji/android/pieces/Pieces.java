package ac.ic.chaturaji.android.pieces;

/**
 * Created by Kadir on March 2.
 */
public abstract class Pieces {

    public int colour;
    public int promotion;
    public abstract boolean[][] valid_moves(int column, int row, Pieces[][] Board);
}
