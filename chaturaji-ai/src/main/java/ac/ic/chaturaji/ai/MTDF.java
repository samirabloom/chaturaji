package ac.ic.chaturaji.ai;

import java.util.ArrayList;

/**
 * Created by dg3213 on 28/03/14.
 */
public class MTDF extends AlphaBeta {
    private static final int MaxSearchSize = 200000;

    public MTDF() {
        super();
    }

    public Move_AI Search(Board_AI board) {
        Move_AI bestMove = null;
        int iterateDepth = 1;
        double firstGuess = 0;

        GameTimer++;
        while(true) {
            iterateDepth++;

            bestMove = MTD_f(board, firstGuess, iterateDepth);
            firstGuess = bestMove.getScore();

            if (iterateDepth >= 15 || (NodesSearched > MaxSearchSize))
                break;
        }

        return bestMove;
    }

    private Move_AI MTD_f(Board_AI board, double guess, int depth) {

        double beta;
        double estimate = guess;
        double upperBound = MAXVAL;
        double lowerBound = MINVAL;

        int maximisingPlayer = board.GetCurrentPlayer();
        Move_AI bestMove = null;

        do {
            if (estimate == lowerBound)
                beta = estimate + 1;
            else
                beta = estimate;

            bestMove = AlphaBetaWithMemory(board, depth, beta - 1, beta, maximisingPlayer, maximisingPlayer);
            estimate = bestMove.getScore();

            if (estimate < beta)
                upperBound = estimate;
            else
                lowerBound = estimate;

        } while (lowerBound < upperBound);

        return bestMove;
    }

    private Move_AI AlphaBetaWithMemory(Board_AI board, int depth, double alpha, double beta, int colour, int maximisingPlayer) {
        Move_AI bestMove = null;
        ArrayList<Move_AI> possMoves = new ArrayList<>();
        double record = MINVAL;
        double score;

        // First generate the moves for the current player.
        validMoves.GenerateMoves(board, possMoves, colour);

        for (Move_AI listMove: possMoves) {
            Board_AI newBoard = board.clone();
            newBoard.ApplyMove(listMove);

            // Maximise the corresponding value returned
            score = alphaBeta(newBoard, depth - 1, alpha, beta, (maximisingPlayer + 1) % 4, maximisingPlayer);
            alpha = Math.max(alpha, score);

            if (score > record) {
                record = score;
                bestMove = listMove;
                bestMove.SetScore(score);

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
