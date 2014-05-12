package ac.ic.chaturaji.ai;

/**
 * Created by dg3213 on 28/03/14.
 */

class TableEntry {
    private long zobristKey;
    private int depth;
    private int flag;
    private double eval;
    private int time;

    public TableEntry() {
        this.flag = -1;
    }

    public TableEntry(long zobrist, int depth, int flag, double eval, int timeStamp) {
        this.zobristKey = zobrist;
        this.depth = depth;
        this.flag = flag;
        this.eval = eval;
        this.time = timeStamp;
    }

    public long getZobrist() {
        return zobristKey;
    }

    public int getFlag() {
        return flag;
    }

    public double getEval() {
        return eval;
    }

    public int getTime() {
        return time;
    }

    public int getDepth() {
        return depth;
    }
}

public class TranspositionTable {
    // Make sure hash table size is prime
    private static final int HASH_SIZE = 131303;
    private TableEntry TransTable[];

    // Construction
    public TranspositionTable() {
        TransTable = new TableEntry[HASH_SIZE];
        for (int i = 0; i < HASH_SIZE; i++) {
            TransTable[i] = new TableEntry();
        }
    }

    // Check to see if there is already a stored board position within the transposition table.
    // If so then we also have a best move for that position (either an upper bound/lower bound/exact value)
    // so copy the values in the table to the input parameter 'move'.
    public boolean FindBoard(AIBoard board, AIMove move) {
        long zobristKey = board.ZobristKey();
        // Find the board's hash position in Table
        int key = Math.abs((int) (zobristKey) % HASH_SIZE);
        TableEntry entry = TransTable[key];

        // Check flag - empty entries are set to 1. If so then then there is not yet an entry corresponding to the board position.
        if (entry.getFlag() == -1)
            return false;

        // Also make sure that the actual board zobrist keys match - not just the hash table entries!
        if (entry.getZobrist() != zobristKey)
            return false;

        // If we find a match then copy into the given move parameter
        move.setScore(entry.getEval());
        move.setEvalType(entry.getFlag());
        move.setDepth(entry.getDepth());
        return true;
    }

    // Save the board using Zobrist's key as identity.
    public boolean SaveBoard(AIBoard board, double evaluation, int flag, int depth, int timeStamp) {
        long zobristKey = board.ZobristKey();
        int key = Math.abs((int) (zobristKey) % HASH_SIZE);

        // If there already exists a better move in the transposition table (i.e. of greater depth)
        // then don't erase it!
        if ((TransTable[key].getFlag() > 0) && (TransTable[key].getDepth() > depth) && (TransTable[key].getTime() >= timeStamp))
            return true;

        // If no better move found then add an entry:
        TransTable[key] = new TableEntry(zobristKey, depth, flag, evaluation, timeStamp);

        return true;
    }
}
