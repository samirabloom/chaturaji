package ac.ic.chaturaji.ai;
import java.util.ArrayList;
import java.lang.Math.*;


/**
 * Created by dg3213 on 04/03/14.
 */
public class AlphaBeta {
    MoveGenerator_AI validMoves;

    public AlphaBeta() {
        validMoves = new MoveGenerator_AI();
    }

    // Search to find the best move for the given colour to the given depth:
    public Move_AI Search(Board_AI board, int colour, int depth) {
        ArrayList<Move_AI> possMoves = new ArrayList<>();

        // First generate the moves for the current player.
        validMoves.GenerateMoves(board, possMoves, colour);

        double alpha = -1000000;
        double beta = 1000000;

        double record = -1000000;

        Move_AI bestMove = null;

        for (Move_AI listMove: possMoves) {
            Board_AI newBoard = board.clone();
            newBoard.ApplyMove(listMove);
            // Maximise the corresponding value returned
            double value = alphaBeta(newBoard, depth - 1, alpha, beta, (colour + 1) % 4, colour);
            if (value > record) {
                record = value;
                bestMove = listMove;
            }
        }
        return bestMove;
    }

    double alphaBeta(Board_AI board, int depth, double alpha, double beta ,int colour, int maximisingPlayer) {

        if (depth == 0 || board.isGameOver() == 0)
            return Evaluate(board.GetMaterialValue(), maximisingPlayer);

        double value;
        ArrayList<Move_AI> possMoves = new ArrayList<>();
        validMoves.GenerateMoves(board, possMoves, colour);

        if (possMoves.size() == 0) {
            // The current player may have lost all its pieces or none of its pieces may move (i.e. pawns blocked).
            // In this case, the player can be ignored, and will return whatever board is optimal for the next
            // depth.
            return alphaBeta(board, depth - 1, alpha, beta, (colour + 1) % 4, maximisingPlayer);
        }

        for (Move_AI listMove: possMoves) {
            Board_AI newBoard = board.clone();
            newBoard.ApplyMove(listMove);
            value = alphaBeta(newBoard, depth - 1, alpha, beta, (colour + 1) % 4, maximisingPlayer);
            if (colour == maximisingPlayer) {
                alpha = Math.max(alpha, value);
                if (beta <= alpha)
                    break;
            }
            else {
                beta = Math.min(beta, value);
                if (beta <= alpha)
                    break;
            }
        }
        if (colour == maximisingPlayer)
            return alpha;
        else
            return beta;
    }

    private double Evaluate(int[] MaterialVal, int maximisingColour) {
        double totalOtherScore = 0;

        for (int i = 1; i < 4; i++) {
            totalOtherScore += MaterialVal[(maximisingColour + i) % 4];
        }

        return MaterialVal[maximisingColour] - (totalOtherScore);
    }
}
