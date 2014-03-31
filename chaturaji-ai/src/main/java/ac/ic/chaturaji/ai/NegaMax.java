package ac.ic.chaturaji.ai;

import java.util.ArrayList;
/*
/**
 * Created by dg3213 on 31/03/14.
 */
/*
public class NegaMax {
    int MINVAL = -1000000;
    int MAXVAL = 1000000;

    MoveGenerator_AI validMoves;
    Evaluation evalFunction;
    TranspositionTable TransTable;
    int GameTimer;
    int NodesSearched;

    public NegaMax() {
        validMoves = new MoveGenerator_AI();
        evalFunction = new Evaluation();
        TransTable = new TranspositionTable();
        GameTimer = 0;
        NodesSearched = 0;
    }

    // Search to find the best move for the given colour to the given depth:
    public Move_AI Search(Board_AI board, int colour, int depth) {
        ArrayList<Move_AI> possMoves = new ArrayList<>();

        // First generate the moves for the current player.
        validMoves.GenerateMoves(board, possMoves, colour);

        double alpha = MINVAL;
        double beta = MAXVAL;
        double record = MINVAL;
        double score;

        Move_AI bestMove = null;

        // Use the GameTimer to determine how far through the game we are. This allows us to put a time stamp
        // on the entries within the transposition table.
        GameTimer++;

        // Use the iterative deepening method combined with aspiration windows for better move ordering:
        for (int iteration = depth; iteration <= depth; iteration++) {
            for (Move_AI listMove: possMoves) {
                Board_AI newBoard = board.clone();
                newBoard.ApplyMove(listMove);

                score = -negaMax(newBoard, iteration - 1, -beta, -alpha, (colour + 1) % 4, colour);
                alpha = Math.max(record, alpha);

                if (score > record) {
                    record = score;
                    bestMove = listMove;
                    bestMove.SetScore(score);
                }
                if (record >= beta)
                    break;
            }
        }
        if(record <= alpha)
            TransTable.SaveBoard(board, record, GameConstants.LOWER_BOUND, depth, GameTimer);
        else if(record >= beta)
            TransTable.SaveBoard(board, record, GameConstants.UPPER_BOUND, depth, GameTimer);
        else
            TransTable.SaveBoard(board, record, GameConstants.UPPER_BOUND, depth, GameTimer);
        return bestMove;
    }

    private double negaMax(Board_AI board, int depth, double alpha, double beta, int colour, int maximisingPlayer) {
        Move_AI testMove = new Move_AI();
        NodesSearched++;
        double score;

        // Check if there is anything suitable within the transposition table first
        if (TransTable.FindBoard(board, testMove) && testMove.getDepth() >= depth) {
            // We have found a move for the current board position. Must now check whether it is relevant
            // (i.e. if it has been resolved to a greater depth than we are currently at and what sort of bound
            // has been placed on its evaluation. Note that we can only use certain bounds depending on whether we are
            // maximising or minimising).

            //System.out.println("Transposition found!");

            int evalType = testMove.getEvaluationType();
            double evaluation = testMove.getScore();

            if (evalType == GameConstants.EXACT_VALUE) {
                return evaluation;
            }

            if (evalType == GameConstants.UPPER_BOUND){
                if (evaluation <= alpha) {
                    return alpha;
                }
            }
            else if (evalType == GameConstants.LOWER_BOUND) {
                if (evaluation >= beta) {
                    return beta;
                }
            }
        }

        if (depth == 0 || board.isGameOver() == 0) {
            //score = QuiescenceSearch(board, alpha, beta, colour, maximisingPlayer);

            score = evalFunction.EvaluateScore(maximisingPlayer, board);

            if(score <= alpha) // We have a lower bound
                TransTable.SaveBoard(board, score, GameConstants.LOWER_BOUND, depth, GameTimer);
            else if(score >= beta) // Upper bound
                TransTable.SaveBoard(board, score, GameConstants.UPPER_BOUND, depth, GameTimer);
            else // An exact value: alpha < score < beta
                TransTable.SaveBoard(board, score, GameConstants.EXACT_VALUE, depth, GameTimer);

            return score;
        }

        double record = MINVAL;
        ArrayList<Move_AI> possMoves = new ArrayList<>();
        validMoves.GenerateMoves(board, possMoves, colour);

        if (possMoves.size() == 0) {
            // The current player may have lost all its pieces or none of its pieces may move (i.e. pawns blocked).
            // In this case, the player can be ignored, and will return whatever board is optimal for the next
            // depth.
            return negaMax(board, depth - 1, alpha, beta, (colour + 1) % 4, maximisingPlayer);
        }

        for (Move_AI listMove: possMoves) {
            Board_AI newBoard = board.clone();
            newBoard.ApplyMove(listMove);
            if (colour != maximisingPlayer)
                score = negaMax(newBoard, depth - 1, alpha, beta, (colour + 1) % 4, maximisingPlayer);
            else
                score = -negaMax(newBoard, depth - 1, -beta, -alpha, (colour + 1) % 4, maximisingPlayer);

            record = Math.max(score, record);
            alpha = Math.max(record, alpha);

            if(record >= beta)
                break;
        }
        if(record <= alpha) // a lowerbound value
            TransTable.SaveBoard(board, record, GameConstants.LOWER_BOUND, depth, GameTimer);
        else if(record >= beta) // an upperbound value
            TransTable.SaveBoard(board, record, GameConstants.UPPER_BOUND, depth, GameTimer);
        else // a true minimax value
            TransTable.SaveBoard(board, record, GameConstants.UPPER_BOUND, depth, GameTimer);
        return record;
    }
}
*/