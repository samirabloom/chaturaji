package ac.ic.chaturaji.ai;

import java.util.ArrayList;

public class Evaluation {
    Board_AI board;
    int currentPlayer;
    ArrayList<Move_AI> validMoves;


    int PawnVal = 6;
    int BoatVal = 4;
    int KnightVal = 4;
    int ElephantVal = 1;
    int KingVal = 1;

    public Evaluation() {}

    public int EvaluateScore(int player, Board_AI theBoard, ArrayList<Move_AI> possMoves) {
        int score = 0;

        board = theBoard;
        currentPlayer = player;
        validMoves = possMoves;
        
        score += board.GetMaterialValue(player);
        score += EvaluatePositions();
        //score += EvaluatePawns();
        score += EvaluateDefense(currentPlayer);
        score += EvaluateOffense(currentPlayer);

        return score;
    }

    private int EvaluatePositions() {
        int score = 0;
        int count = 0;

        if (board.GetBitBoard(GameConstants.KNIGHT + currentPlayer) == 0)
            count++;
        if (board.GetBitBoard(GameConstants.BOAT + currentPlayer) == 0)
            count++;

        for (int i = 0; i < 64; i++) {
            if (count >= 2)
                break;

            if ((GameConstants.SquareBits[i] & board.GetBitBoard(GameConstants.KNIGHT + currentPlayer)) != 0) {
                score += GameConstants.KnightTable[i];
                count++;
            }
            if ((GameConstants.SquareBits[i] & board.GetBitBoard(GameConstants.BOAT + currentPlayer)) != 0) {
                score += GameConstants.BoatTable[i];
                count++;
            }
        }
    return score;
    }

    private int EvaluatePawns() {
        int score = 0;

        if (board.GetBitBoard(GameConstants.PAWN + currentPlayer) == 0)
            return score;

        for (int i = 0; i < 64; i++)
            if ((GameConstants.SquareBits[i] & board.GetBitBoard(GameConstants.PAWN + currentPlayer)) != 0) {
                switch (currentPlayer) {
                    case GameConstants.YELLOW: {
                        score += GameConstants.YellowPawnTable[i];

                        // Doubled pawns are useless
                        if (DoubledPawn(i, GameConstants.YELLOW))
                            score -= 20;

                        // Encourage developing pawns that are almost towards the end of the board.
                        if (((i % 8) == 6 || (i % 8) == 7) && (isDefended(i, currentPlayer) > isAttacked(i, currentPlayer)))
                            score += 100;
                    } break;
                    case GameConstants.BLUE: {
                        score += GameConstants.BluePawnTable[i];

                        if (DoubledPawn(i, GameConstants.BLUE))
                            score -= 20;

                        if (i >= 48 && (isDefended(i, currentPlayer) > isAttacked(i, currentPlayer)))
                            score += 100;
                    } break;
                    case GameConstants.RED: {
                        score += GameConstants.RedPawnTable[i];

                        if (DoubledPawn(i, GameConstants.RED))
                            score -= 20;

                        if (((i % 8) == 0 || (i % 8) == 1) && (isDefended(i, currentPlayer) > isAttacked(i, currentPlayer)))
                            score += 100;
                    } break;
                    case GameConstants.GREEN: {
                        score += GameConstants.GreenPawnTable[i];

                        if (DoubledPawn(i, GameConstants.BLUE))
                            score -= 20;

                        if (i <= 15 && (isDefended(i, currentPlayer) > isAttacked(i, currentPlayer)))
                            score += 100;
                    } break;


                }
            }
        return 0;
    }

    private boolean DoubledPawn(int square, int colour) {

        switch (colour % 2) {
            case 0: {
                if ((GameConstants.SquareBits[square - 1] & board.GetBitBoard(GameConstants.PAWN + colour)) != 0)
                    return true;
                if ((GameConstants.SquareBits[square + 1] & board.GetBitBoard(GameConstants.PAWN + colour)) != 0)
                    return true;
            } break;
            case 1: {
                if ((GameConstants.SquareBits[square + 8] & board.GetBitBoard(GameConstants.PAWN + colour)) != 0)
                    return true;
                if ((GameConstants.SquareBits[square - 8] & board.GetBitBoard(GameConstants.PAWN + colour)) != 0)
                    return true;
            } break;
        }
    return false;
    }

    private int EvaluateDefense(int colour) {
        // Wish to make sure that the pieces are backed up:
        int score = 0;

        score += CheckPieceDefense(GameConstants.KING, colour);
        score += CheckPieceDefense(GameConstants.BOAT, colour);
        score += CheckPieceDefense(GameConstants.KNIGHT, colour);
        score += CheckPieceDefense(GameConstants.ELEPHANT, colour);

        return score;
    }
    
    private int EvaluateOffense(int colour) {
        int score = 0;
        int count = 0;
        
        for (Move_AI listMove: validMoves)
            if (listMove.getType() == GameConstants.CAPTURE) {
                int destination =  listMove.getDest();
                int piece = listMove.getCaptured();

                // Promote positions with lots of capture potential, count the number of captures and add them at the end.
                count++;

                int defendingPieces = isAttacked(destination, colour);
                int currentColourAttacking = isDefended(destination, colour);

                // Check if the destination is covered by other players, if not then the piece is hanging & free to take
                // without consequence.
                if(defendingPieces == 0) {
                    switch (piece) {
                        case GameConstants.PAWN: score += GameConstants.PAWN_VALUE; break;
                        case GameConstants.BOAT: score += GameConstants.BOAT_VALUE; break;
                        case GameConstants.KNIGHT: score += GameConstants.KNIGHT_VALUE; break;
                        case GameConstants.ELEPHANT: score += GameConstants.ELEPHANT_VALUE; break;
                        case GameConstants.KING: score += GameConstants.KING_VALUE; break;
                    }
                }
                // Otherwise promote ganging up on a certain piece:
                else if (currentColourAttacking > defendingPieces) {
                    score += (currentColourAttacking - defendingPieces) * 5;
                }
            }

        return score + (count * 2);
    }

    
    private int CheckPieceDefense(int piece, int colour) {
        // Check to see if the given piece is being attacked. Return penalties if so.
        int position;
        int score = 0;

        if (board.GetBitBoard(piece + colour) == 0)
            return 0;

        for (position = 0; position < 64; position++) {
            if ((GameConstants.SquareBits[position] & board.GetBitBoard(piece + colour)) != 0)
                break;
        }

        int defense = isDefended(position, colour);
        int attacked = isAttacked(position, colour);

        score += (defense - attacked);
        // Check if the piece is being overwhelmed, penalise heavily if so:
        if (defense < attacked) {
            score -= (attacked - defense) * 5;
        }

        return score;
    }

    // Check whether a certain colour's position is defended by its own pieces:
    private int isDefended(int position, int colour) {
        int count = 0;

        if (CheckCover(position, GameConstants.KNIGHT, colour))
            count += KnightVal;
        if (CheckCover(position, GameConstants.KING, colour))
            count += KingVal;
        if (CheckCover(position, GameConstants.BOAT, colour))
            count += BoatVal;
        if (CheckCover(position, GameConstants.ELEPHANT, colour))
            count += ElephantVal;

        count += PawnVal * PawnCover(position, colour);

        return count;
    }

    // Check whether a certain colour's position is being attacked by other player's pieces:
    private int isAttacked(int position, int colour) {
        int count = 0;
        int enemy;

        for (int i = 1; i < 4; i++) {
            enemy = (colour + i) % 4;

            if (CheckCover(position, GameConstants.KNIGHT, enemy))
                count += KnightVal;
            if (CheckCover(position, GameConstants.KING, enemy))
                count += KingVal;
            if (CheckCover(position, GameConstants.BOAT, enemy))
                count += BoatVal;
            if (CheckCover(position, GameConstants.ELEPHANT, enemy))
                count += ElephantVal;

            count += PawnVal * PawnCover(position, enemy);
        }
        return count;
    }

    // Check if a given square is within the attacking/ defending range of a given piece:
    private boolean CheckCover(int position, int piece, int colour) {
        if (board.GetBitBoard(piece + colour) == 0)
            return false;

        for (int i = 0; i < 64; i++) {
            if ((GameConstants.SquareBits[i] & board.GetBitBoard(piece + colour)) != 0) {

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
                            allPieces |= board.GetBitBoard(GameConstants.ALL_PIECES + col);
                        }

                        for (int direction = 0; direction < PieceMoves.ElephantMoves[i].length; direction++)
                            for (int ray = 0; ray < PieceMoves.ElephantMoves[i][direction].length; ray++) {

                                int destination = PieceMoves.ElephantMoves[i][direction][ray];

                                if (destination == position)
                                    return true;

                                if ((allPieces & GameConstants.SquareBits[destination]) != 0)
                                    break;
                            }
                    } break;
                }
            }
        }
        return false;
    }

    private int PawnCover(int position, int colour) {
        if (board.GetBitBoard(GameConstants.PAWN + colour) == 0)
            return 0;

        int count = 0;

        for (int i = 0; i < 64; i++) {
            if ((GameConstants.SquareBits[i] & board.GetBitBoard(GameConstants.PAWN + colour)) != 0) {
                switch (colour) {
                    case 0: {
                        if ((i + 9) == position || (i - 7) == position)
                            count++;
                    } break;
                    case 1: {
                        if ((i + 9) == position || (i + 7) == position)
                            count++;
                    } break;
                    case 2: {
                        if ((i - 9) == position || (i + 7) == position)
                            count++;
                    } break;
                    case 3: {
                        if ((i - 9) == position || (i - 7) == position)
                            count++;
                    } break;
                }
            }
        }
    return count;
    }

}
