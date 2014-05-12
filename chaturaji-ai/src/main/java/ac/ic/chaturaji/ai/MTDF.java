package ac.ic.chaturaji.ai;

import java.util.ArrayList;

/**
 * @author dg3213
 */
public class MTDF extends AlphaBeta {
    private static final int MaxSearchSize = 250000;

    public AIMove Search(AIBoard board, int maxIterateDepth) {
        AIMove bestMove = null;
        int iterateDepth;
        double firstGuess = 0;

        GameTimer++;
        for (iterateDepth = 2; iterateDepth <= maxIterateDepth; iterateDepth++) {
            bestMove = MTD_f(board, firstGuess, iterateDepth);

            if (bestMove != null)
                firstGuess = bestMove.getScore();

            if (NodesSearched > MaxSearchSize) {
                break;
            }
        }
        return bestMove;
    }

    private AIMove MTD_f(AIBoard board, double guess, int depth) {

        double beta;
        double estimate = guess;
        double upperBound = MAXVAL;
        double lowerBound = MINVAL;

        int maximisingPlayer = board.getCurrentPlayer();
        AIMove bestMove;

        do {
            if (estimate == lowerBound)
                beta = estimate + 1;
            else
                beta = estimate;

            bestMove = AlphaBetaWithMemory(board, depth, beta - 1, beta, maximisingPlayer, maximisingPlayer);
            if (bestMove != null)
                estimate = bestMove.getScore();

            if (estimate < beta)
                upperBound = estimate;
            else
                lowerBound = estimate;

        } while (lowerBound < upperBound);

        return bestMove;
    }

    private AIMove AlphaBetaWithMemory(AIBoard board, int depth, double alpha, double beta, int colour, int maximisingPlayer) {
        AIMove bestMove = null;
        ArrayList<AIMove> possMoves = new ArrayList<>();
        double record = MINVAL;
        double score;

        validMoves.generateMoves(board, possMoves, colour);

        for (AIMove listMove : possMoves) {
            AIBoard newBoard = board.clone();
            newBoard.ApplyMove(listMove);

            score = alphaBeta(newBoard, depth - 1, alpha, beta, (maximisingPlayer + 1) % 4, maximisingPlayer);
            alpha = Math.max(alpha, score);

            if (score > record) {
                record = score;
                bestMove = listMove;
                bestMove.setScore(score);

                if (record >= beta) {
                    TransTable.SaveBoard(board, record, GameConstants.LOWER_BOUND, depth, GameTimer);
                    return bestMove;
                }
                if (record > alpha) {
                    TransTable.SaveBoard(board, record, GameConstants.EXACT_VALUE, depth, GameTimer);
                }
            }
        }
        return bestMove;
    }
}
