package ac.ic.chaturaji.ai;

import java.util.ArrayList;

/**
 * @author dg3213
 */
public class Evaluation {
    AIMoveGenerator moveGenerator;

    int PawnVal = 6;
    int BoatVal = 4;
    int KnightVal = 4;
    int ElephantVal = 1;
    int KingVal = 1;

    public Evaluation() {
        moveGenerator = new AIMoveGenerator();
    }

    // Weigh up the different factors in the evaluation.
    // Material is by the far the most important factor.
    public double EvaluateScore(int player, AIBoard board) {
        double score;

        score = EvaluateMaterial(player, board);
        score += 0.5 * EvaluatePawns(player, board);
        score += 0.5 * EvaluatePosition(player, board);
        //score += 0.3 * EvaluateDefense(player, board);
        score += 0.1 * EvaluateMobility(player, board);

        return score;
    }

    private double EvaluateMaterial(int maximisingColour, AIBoard board) {
        int kingScore = 0;
        int elephantScore = 0;
        int knightScore = 0;
        int boatScore = 0;
        int pawnScore = 0;

        int kingCount[] = {0, 0, 0, 0};
        int elephantCount[] = {0, 0, 0, 0};
        int knightCount[] = {0, 0, 0, 0};
        int boatCount[] = {0, 0, 0, 0};
        int pawnCount[] = {0, 0, 0, 0};

        for (int colour = 0; colour < 4; colour++) {
            if (board.getBitBoard(GameConstants.KING + colour) != 0)
                kingCount[colour]++;
            if (board.getBitBoard(GameConstants.ELEPHANT + colour) != 0)
                elephantCount[colour]++;
            if (board.getBitBoard(GameConstants.KNIGHT + colour) != 0)
                knightCount[colour]++;
            if (board.getBitBoard(GameConstants.BOAT + colour) != 0)
                boatCount[colour]++;

            if (board.getBitBoard(GameConstants.PAWN + colour) == 0)
                continue;
            for (int i = 0; i < 64; i++) {
                if ((board.getBitBoard(GameConstants.PAWN + colour) & GameConstants.SquareBits[i]) != 0)
                    pawnCount[colour]++;
            }
        }

        for (int i = 1; i < 4; i++) {
            kingScore += kingCount[(maximisingColour + i) % 4];
            elephantScore += elephantCount[(maximisingColour + i) % 4];
            knightScore += knightCount[(maximisingColour + i) % 4];
            boatScore += boatCount[(maximisingColour + i) % 4];
            pawnScore += pawnCount[(maximisingColour + i) % 4];
        }

        return 12 * (kingCount[maximisingColour] - kingScore) + 7 * (elephantCount[maximisingColour] - elephantScore)
                + 4 * (knightCount[maximisingColour] - knightScore) + 4 * (boatCount[maximisingColour] - boatScore)
                + (pawnCount[maximisingColour] - pawnScore);
    }

    private double EvaluatePawns(int maximisingColour, AIBoard board) {
        int score = 0;

        if (board.getBitBoard(GameConstants.PAWN + maximisingColour) == 0)
            return score;

        for (int i = 0; i < 64; i++)
            if ((GameConstants.SquareBits[i] & board.getBitBoard(GameConstants.PAWN + maximisingColour)) != 0) {
                switch (maximisingColour) {
                    case GameConstants.YELLOW: {
                        score += GameConstants.YellowPawnTable[i] / 10;

                        // Doubled pawns are useless
                        if (DoubledPawn(i, GameConstants.YELLOW, board))
                            score--;
                    }
                    break;
                    case GameConstants.BLUE: {
                        score += GameConstants.BluePawnTable[i] / 10;

                        if (DoubledPawn(i, GameConstants.BLUE, board))
                            score--;
                    }
                    break;
                    case GameConstants.RED: {
                        score += GameConstants.RedPawnTable[i] / 10;

                        if (DoubledPawn(i, GameConstants.RED, board))
                            score--;
                    }
                    break;
                    case GameConstants.GREEN: {
                        score += GameConstants.GreenPawnTable[i] / 10;

                        if (DoubledPawn(i, GameConstants.BLUE, board))
                            score--;
                    }
                    break;
                }
            }
        return score;
    }

    private boolean DoubledPawn(int square, int colour, AIBoard board) {

        switch (colour % 2) {
            case 0: {
                if (square >= 1 && ((GameConstants.SquareBits[square - 1] & board.getBitBoard(GameConstants.PAWN + colour)) != 0))
                    return true;
                if (square < GameConstants.ALL_SQUARES - 1 && ((GameConstants.SquareBits[square + 1] & board.getBitBoard(GameConstants.PAWN + colour)) != 0))
                    return true;
            }
            break;
            case 1: {
                if (square < GameConstants.ALL_SQUARES - 8 && (GameConstants.SquareBits[square + 8] & board.getBitBoard(GameConstants.PAWN + colour)) != 0)
                    return true;
                if (square >= 8 && ((GameConstants.SquareBits[square - 8] & board.getBitBoard(GameConstants.PAWN + colour)) != 0))
                    return true;
            }
            break;
        }
        return false;
    }

    private double EvaluatePosition(int maximisingColour, AIBoard board) {
        int score = 0;
        int count = 0;

        if (board.getBitBoard(GameConstants.KNIGHT + maximisingColour) == 0)
            count++;
        if (board.getBitBoard(GameConstants.BOAT + maximisingColour) == 0)
            count++;

        for (int i = 0; i < 64; i++) {
            if (count >= 2)
                break;

            if ((GameConstants.SquareBits[i] & board.getBitBoard(GameConstants.KNIGHT + maximisingColour)) != 0) {
                score += GameConstants.KnightTable[i] / 100;
                count++;
            }
            if ((GameConstants.SquareBits[i] & board.getBitBoard(GameConstants.BOAT + maximisingColour)) != 0) {
                score += GameConstants.BoatTable[i] / 100;
                count++;
            }
        }
        return score;
    }

    private double EvaluateMobility(int maximisingColour, AIBoard board) {
        ArrayList<AIMove> possMoves = new ArrayList<>();

        int currentMobility;
        int otherMobility = 0;

        moveGenerator.generateMoves(board, possMoves, maximisingColour);
        currentMobility = possMoves.size();

        for (int i = 1; i < 4; i++) {
            moveGenerator.generateMoves(board, possMoves, (maximisingColour + i) % 4);
            otherMobility += possMoves.size();
        }

        return currentMobility - otherMobility;
    }

    private int EvaluateDefense(int colour, AIBoard board) {
        // Wish to make sure that the pieces are backed up:
        int score = 0;

        for (int i = 0; i < 64; i++) {
            if ((board.getBitBoard(GameConstants.ALL_PIECES + colour) & GameConstants.SquareBits[i]) != 0)
                if (CheckCover(board, i, GameConstants.KING, colour))
                    score += KingVal;
            if (CheckCover(board, i, GameConstants.KNIGHT, colour))
                score += KnightVal;
            if (CheckCover(board, i, GameConstants.ELEPHANT, colour))
                score += ElephantVal;
            if (CheckCover(board, i, GameConstants.BOAT, colour))
                score += BoatVal;
            score += PawnVal * PawnCover(board, i, colour);
        }
        return (score / 10);
    }

    // Check if a given square is within the attacking/ defending range of a given colour's piece:
    private boolean CheckCover(AIBoard board, int position, int piece, int colour) {
        if (board.getBitBoard(piece + colour) == 0)
            return false;

        for (int i = 0; i < 64; i++) {
            if ((GameConstants.SquareBits[i] & board.getBitBoard(piece + colour)) != 0) {

                switch (piece) {
                    case GameConstants.KNIGHT: {
                        for (int ray = 0; ray < PieceMoves.KnightMoves[i].length; ray++) {
                            if (PieceMoves.KnightMoves[i][ray] == position)
                                return true;
                        }
                    }
                    break;
                    case GameConstants.KING: {
                        for (int ray = 0; ray < PieceMoves.KingMoves[i].length; ray++) {
                            if (PieceMoves.KingMoves[i][ray] == position)
                                return true;
                        }
                    }
                    break;
                    case GameConstants.BOAT: {
                        for (int ray = 0; ray < PieceMoves.BoatMoves[i].length; ray++) {
                            if (PieceMoves.KingMoves[i][ray] == position)
                                return true;
                        }
                    }
                    break;
                    case GameConstants.ELEPHANT: {
                        long allPieces = 0;

                        for (int col = 0; col < 4; col++) {
                            allPieces |= board.getBitBoard(GameConstants.ALL_PIECES + col);
                        }

                        for (int direction = 0; direction < PieceMoves.ElephantMoves[i].length; direction++)
                            for (int ray = 0; ray < PieceMoves.ElephantMoves[i][direction].length; ray++) {

                                int destination = PieceMoves.ElephantMoves[i][direction][ray];

                                if (destination == position)
                                    return true;

                                if ((allPieces & GameConstants.SquareBits[destination]) != 0)
                                    break;
                            }
                    }
                    break;
                }
            }
        }
        return false;
    }

    private int PawnCover(AIBoard board, int position, int colour) {
        if (board.getBitBoard(GameConstants.PAWN + colour) == 0)
            return 0;

        int count = 0;

        for (int i = 0; i < 64; i++) {
            if ((GameConstants.SquareBits[i] & board.getBitBoard(GameConstants.PAWN + colour)) != 0) {
                switch (colour) {
                    case 0: {
                        if ((i + 9) == position || (i - 7) == position)
                            count++;
                    }
                    break;
                    case 1: {
                        if ((i + 9) == position || (i + 7) == position)
                            count++;
                    }
                    break;
                    case 2: {
                        if ((i - 9) == position || (i + 7) == position)
                            count++;
                    }
                    break;
                    case 3: {
                        if ((i - 9) == position || (i - 7) == position)
                            count++;
                    }
                    break;
                }
            }
        }
        return count;
    }
}

