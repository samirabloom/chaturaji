package ac.ic.chaturaji.ai;

import java.util.Scanner;
import java.lang.*;

public class Game_AI {
    Player_AI Players[];
    Board_AI board;
    Scanner read;
    char[] input;

    public Game_AI() {
        Initialise();
        //Run();
    }

    public void Initialise()
    {
        board = new Board_AI();

        read = new Scanner(System.in);
        input = new char[20];

        int points = 0;
        int[] kings = new int[] {0,0,0,0};

        Players = new Player_AI[4];
        Players[GameConstants.YELLOW] = new PlayerComp(GameConstants.YELLOW, points, kings);
        Players[GameConstants.BLUE] = new PlayerComp(GameConstants.BLUE, points, kings);
        Players[GameConstants.RED] = new PlayerHuman(GameConstants.RED, points, kings);
        Players[GameConstants.GREEN] = new PlayerComp(GameConstants.GREEN, points, kings);
    }


    public void Run()
    {
        Player_AI Player;
        PlayerHuman CurrentPlayer;
        PlayerComp AIPlayer;
        Move_AI move;

        do
        {
            Player = Players[board.GetCurrentPlayer()];
            // Show the current game board
            board.Print();

            //CurrentPlayer = (PlayerHuman)Players[board.GetCurrentPlayer()];
            //move = GetMove(board, CurrentPlayer);

            if (Player.GetPlayerType() == GameConstants.HUMAN) {
                // Ask the next player for a move
                CurrentPlayer = (PlayerHuman)Players[board.GetCurrentPlayer()];
                move = GetMove(board, CurrentPlayer);
            }
            else {
                // AI player, just generate a move.
                AIPlayer = (PlayerComp)Players[board.GetCurrentPlayer()];
                move = AIPlayer.GetMove(board);
            }


            // Update the bit boards.
            board.ApplyMove(move);

        } while(move.getType() != GameConstants.RESIGN);

        System.out.println("Game Over!");
    }

    private Move_AI GetMove(Board_AI theBoard, PlayerHuman CurrentPlayer) {

        boolean move_complete = false;
        int colour = theBoard.GetCurrentPlayer();
        int source, dest, piece;

        do{
            System.out.println(GameConstants.PlayerStrings[colour] + " to move next. Please enter your move:" );

            String move = read.nextLine();
            input = move.toCharArray();

            source = ((int)(Character.toLowerCase(input[0])) - 'a') + (8 - ((int)(input[1]) - '0')) * 8;
            dest = ((int)(Character.toLowerCase(input[3])) - 'a') + (8 - ((int)(input[4]) - '0')) * 8;

            if ((source < 0 ) || (source > 63 )){
                System.out.println( "Illegal source square " + source);
                continue;
            }
            if ((dest < 0 ) || (dest > 63)){
                System.out.println( "Illegal destination square " + dest);
                continue;
            }

            piece = theBoard.FindPieceColour(source, colour);

            if (piece == GameConstants.EMPTY_SQUARE ){
                System.out.println( "You don't have a piece at square " + source + "!");
                continue;
            }

            if (theBoard.FindPieceColour(dest, colour) != GameConstants.EMPTY_SQUARE ) {
                System.out.println( "You can't capture your own piece!" );
                continue;
            }

            move_complete = true;

        } while (!move_complete);

        return CurrentPlayer.GetMove(theBoard, source, dest);
    }
}