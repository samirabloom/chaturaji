package ac.ic.chaturaji.ai;

import java.util.ArrayList;

/**
 * @author dg3213
 */
public class MaxN {
    AIMoveGenerator validMoves;

    public MaxN() {
        validMoves = new AIMoveGenerator();
    }

    // Search to find the best move for the given colour to the given depth:
    public AIMove Search(AIBoard board, int colour, int depth) {
        ArrayList<AIMove> possMoves = new ArrayList<>();

        // First generate the moves for the current player.
        validMoves.generateMoves(board, possMoves, colour);

        double bestVal = -1000000;
        AIMove bestMove = null;
        double[] returnEval = null;

        for (AIMove listMove : possMoves) {
            AIBoard newBoard = board.clone();
            newBoard.ApplyMove(listMove);
            double[] value = MinimaxN(newBoard, depth - 1, (colour + 1) % 4);
            if (value[colour] > bestVal) {
                bestVal = value[colour];
                returnEval = value;
                bestMove = listMove;
            } else if (value[colour] == bestVal) {
                if (sum(value) < sum(returnEval)) {
                    bestVal = value[colour];
                    returnEval = value;
                    bestMove = listMove;
                }
            }
        }
        return bestMove;
    }

    private double[] MinimaxN(AIBoard board, int depth, int colour) {

        if (depth == 0 || board.isGameOver() == 0)
            return Evaluate(board.GetMaterialValue());

        ArrayList<AIMove> possMoves = new ArrayList<>();
        validMoves.generateMoves(board, possMoves, colour);

        if (possMoves.size() == 0) {
            // The current player may have lost all its pieces or none of its pieces may move (i.e. pawns blocked).
            // In this case, the player can be ignored, and will return whatever board is optimal for the next
            // depth.
            return MinimaxN(board, depth - 1, (colour + 1) % 4);
        }

        double bestVal = -1000000;
        double[] returnEval = null;

        for (AIMove listMove : possMoves) {
            AIBoard newBoard = board.clone();
            newBoard.ApplyMove(listMove);
            double[] value = MinimaxN(newBoard, depth - 1, (colour + 1) % 4);
            if (value[colour] > bestVal) {
                bestVal = value[colour];
                returnEval = value;
            } else if (value[colour] == bestVal) {
                if (sum(value) < sum(returnEval)) {
                    bestVal = value[colour];
                    returnEval = value;
                }
            }
        }
        return returnEval;
    }

    private double sum(double[] array) {
        double total = 0;

        for (double element : array) {
            total += element;
        }
        return total;
    }

    private double[] Evaluate(int[] MaterialVal) {
        double[] evaluated = new double[4];

        for (int i = 0; i < 4; i++) {
            double[] others = new double[]{(MaterialVal[(i + 1) % 4]), MaterialVal[(i + 2) % 4], MaterialVal[(i + 3) % 4]};
            evaluated[i] = MaterialVal[i] - (1 / 3) * sum(others);
        }
        return evaluated;
    }
}
