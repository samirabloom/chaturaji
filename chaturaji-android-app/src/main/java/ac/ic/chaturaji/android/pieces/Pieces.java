package ac.ic.chaturaji.android.pieces;

import java.io.Serializable;

/**
 * @author Kadir
 */
public abstract class Pieces implements Serializable {

    public int colour;
    public int promotion;

    public abstract boolean[][] valid_moves(int column, int row, Pieces[][] Board);
}
