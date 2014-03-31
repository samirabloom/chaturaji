package ac.ic.chaturaji.ai;

import java.util.ArrayList;

/**
 * Created by dg3213 on 31/03/14.
 */
public class QuiescenceSearch extends AlphaBeta {

    private double QuiescenceSearch(Board_AI board, double alpha, double beta ,int colour, int maximisingPlayer) {
        Move_AI testMove = new Move_AI();
        NodesSearched++;
        double score;

        // Check if there is anything suitable within the transposition table first.
        if (TransTable.FindBoard(board, testMove) && testMove.getDepth() >= 0) {
            int evalType = testMove.getEvaluationType();
            double evaluation = testMove.getScore();

            switch (evalType) {
                case GameConstants.EXACT_VALUE:
                    return evaluation;
                case GameConstants.UPPER_BOUND:
                    if (evaluation < beta)
                        beta = evaluation;
                    break;
                case GameConstants.LOWER_BOUND:
                    if (alpha < evaluation)
                        alpha = evaluation;
                    break;
            }
            if (alpha >= beta)
                return evaluation;
        }

        double record = evalFunction.EvaluateScore(maximisingPlayer, board);

        if (colour == maximisingPlayer) {
            if(record >= beta)
                return beta;
            if(alpha < record)
                alpha = record;
        }
        else {
            if(record <= alpha)
                return alpha;
            if(record < beta)
                beta = record;
        }

        ArrayList<Move_AI> possMoves = new ArrayList<>();
        validMoves.GenerateMoves(board, possMoves, colour);

        for (Move_AI listMove: possMoves) {
            // Only look at captures from here!
            if (listMove.getType() != GameConstants.CAPTURE)
                continue;

            Board_AI newBoard = board.clone();
            newBoard.ApplyMove(listMove);
            score = QuiescenceSearch(newBoard, alpha, beta, (colour + 1) % 4, maximisingPlayer);

            if (colour == maximisingPlayer) {
                alpha = Math.max(alpha, score);
                if (beta <= score) {
                    TransTable.SaveBoard(board, beta, GameConstants.LOWER_BOUND, 0, GameTimer);
                    return beta;
                }
                // Otherwise we have a score between alpha and beta - save this as en exact value.
                if (score > alpha) {
                    TransTable.SaveBoard(board, score, GameConstants.EXACT_VALUE, 0, GameTimer);
                }
                return alpha;
            }
            else {
                beta = Math.min(beta, score);
                if (score <= alpha) {
                    TransTable.SaveBoard(board, alpha, GameConstants.UPPER_BOUND, 0, GameTimer);
                    return alpha;
                }
                if (score < beta) {
                    TransTable.SaveBoard(board, score, GameConstants.EXACT_VALUE, 0, GameTimer);
                }
                return beta;
            }
        }
        if (colour == maximisingPlayer)
            return alpha;
        else
            return beta;
    }
}
