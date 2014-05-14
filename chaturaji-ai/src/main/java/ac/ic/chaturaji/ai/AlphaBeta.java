package ac.ic.chaturaji.ai;

import java.util.ArrayList;


/**
 * @author dg3213
 */
public class AlphaBeta {
    int MINVAL = -1000000;
    int MAXVAL = 1000000;

    AIMoveGenerator validMoves;
    Evaluation evalFunction;
    TranspositionTable TransTable;
    int GameTimer;
    int NodesSearched;

    public AlphaBeta() {
        validMoves = new AIMoveGenerator();
        evalFunction = new Evaluation();
        TransTable = new TranspositionTable();
        GameTimer = 0;
        NodesSearched = 0;
    }

    // Search to find the best move for the given colour to the given depth:
    public AIMove Search(AIBoard board, int colour, int depth) {
        ArrayList<AIMove> possMoves = new ArrayList<>();

        // First generate the moves for the current player.
        validMoves.generateMoves(board, possMoves, colour);

        double alpha = MINVAL;
        double beta = MAXVAL;
        double record = MINVAL;
        double score;

        AIMove bestMove = null;

        // Use the GameTimer to determine how far through the game we are. This allows us to put a time stamp
        // on the entries within the transposition table.
        GameTimer++;

        // Use the iterative deepening method combined with aspiration windows for better move ordering:
        for (int iteration = depth; iteration <= depth; iteration++) {
            for (AIMove listMove : possMoves) {
                AIBoard newBoard = board.clone();
                newBoard.ApplyMove(listMove);

                // Maximise the corresponding value returned
                score = alphaBeta(newBoard, iteration - 1, alpha, beta, (colour + 1) % 4, colour);

                // If score is outside the given window then we must call the next alphaBeta with the
                // original values. Otherwise we may close the window for added efficiency:
                if (score <= alpha || score >= beta) {
                    alpha = MINVAL;
                    beta = MAXVAL;
                } else {
                    alpha = score - 10;
                    beta = score + 10;
                }

                if (score > record) {
                    record = score;
                    bestMove = listMove;
                    bestMove.setScore(score);
                }
            }
        }
        return bestMove;
    }

    public double alphaBeta(AIBoard board, int depth, double alpha, double beta, int colour, int maximisingPlayer) {

        AIMove testMove = new AIMove();
        NodesSearched++;
        double score;

        // Check if there is anything suitable within the transposition table first
        if (TransTable.FindBoard(board, testMove) && testMove.getDepth() >= depth) {
            // We have found a move for the current board position. Must now check whether it is relevant
            // (i.e. if it has been resolved to a greater depth than we are currently at and what sort of bound
            // has been placed on its evaluation. Note that we can only use certain bounds depending on whether we are
            // maximising or minimising).

            int evalType = testMove.getEvaluationType();
            double evaluation = testMove.getScore();

            if (evalType == GameConstants.EXACT_VALUE) {
                return evaluation;
            }

            if (evalType == GameConstants.UPPER_BOUND) {
                if (evaluation <= alpha) {
                    return alpha;
                }
            } else if (evalType == GameConstants.LOWER_BOUND) {
                if (evaluation >= beta) {
                    return beta;
                }
            }
        }

        if (depth == 0 || board.isGameOver() == 0) {
            //score = QuiescenceSearch(board, alpha, beta, colour, maximisingPlayer);

            score = evalFunction.EvaluateScore(maximisingPlayer, board);

            if (score <= alpha) // We have a lower bound
                TransTable.SaveBoard(board, score, GameConstants.LOWER_BOUND, depth, GameTimer);
            else if (score >= beta) // Upper bound
                TransTable.SaveBoard(board, score, GameConstants.UPPER_BOUND, depth, GameTimer);
            else // An exact value: alpha < score < beta
                TransTable.SaveBoard(board, score, GameConstants.EXACT_VALUE, depth, GameTimer);

            return score;
        }


        ArrayList<AIMove> possMoves = new ArrayList<>();
        validMoves.generateMoves(board, possMoves, colour);

        if (possMoves.size() == 0) {
            // The current player may have lost all its pieces or none of its pieces may move (i.e. pawns blocked).
            // In this case, the player can be ignored, and will return whatever board is optimal for the next
            // depth.
            return alphaBeta(board, depth - 1, alpha, beta, (colour + 1) % 4, maximisingPlayer);
        }

        for (AIMove listMove : possMoves) {
            AIBoard newBoard = board.clone();
            newBoard.ApplyMove(listMove);
            score = alphaBeta(newBoard, depth - 1, alpha, beta, (colour + 1) % 4, maximisingPlayer);

            if (colour == maximisingPlayer) {
                alpha = Math.max(alpha, score);
                if (beta <= score) {
                    TransTable.SaveBoard(board, beta, GameConstants.LOWER_BOUND, depth, GameTimer);
                    return beta;
                }
                // Otherwise we may have a score between alpha and beta - save this as en exact value.
                if (score > alpha) {
                    TransTable.SaveBoard(board, score, GameConstants.EXACT_VALUE, depth, GameTimer);
                }
            } else {
                beta = Math.min(beta, score);
                if (score <= alpha) {
                    TransTable.SaveBoard(board, alpha, GameConstants.UPPER_BOUND, depth, GameTimer);
                    return alpha;
                }
                if (score < beta) {
                    TransTable.SaveBoard(board, score, GameConstants.EXACT_VALUE, depth, GameTimer);
                }
            }
        }
        if (colour == maximisingPlayer)
            return alpha;
        else
            return beta;
    }
}


    /*
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
*/

