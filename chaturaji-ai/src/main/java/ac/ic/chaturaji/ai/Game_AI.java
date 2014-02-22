package ac.ic.chaturaji.ai;

public class Game_AI
{
  Player_AI Players[];
  Board_AI GameBoard;

  public Game_AI(Player_AI players[], Board_AI board) {
      Players = players;
      GameBoard = board;
  }

  public boolean InitializeGame() throws Exception
  {
    GameBoard = new Board_AI();
    GameBoard.StartBoard();

    Move_AI move = new Move_AI();
    int key;

    int points = 0;
    int[] kings = {0,0,0,0};

    Players = new Player_AI[ 4 ];
    Players[GameConstants_AI.YELLOW] = new PlayerAI_AI(GameConstants_AI.YELLOW,points,kings);
    Players[GameConstants_AI.BLUE] = new PlayerAI_AI(GameConstants_AI.BLUE,points,kings);
    Players[GameConstants_AI.RED] = new PlayerAI_AI(GameConstants_AI.RED,points,kings);
    Players[GameConstants_AI.GREEN] = new PlayerAI_AI(GameConstants_AI.GREEN,points,kings);

    return true;
  }

  public boolean RunGame() throws Exception
  {
    Player_AI CurrentPlayer;
    PlayerAI_AI AIPlayer;
    Move_AI move;

    do
    {
      // Show the current game board
      GameBoard.Print();

      // Ask the next player for a move
      AIPlayer = (PlayerAI_AI)Players[GameBoard.GetCurrentPlayer()];
      move = AIPlayer.GetMove(GameBoard);
      System.out.print(GameConstants_AI.PlayerStrings[GameBoard.GetCurrentPlayer() ] );
      System.out.print( " selects move: " );
      move.Print();

      // Change the state of the game accordingly
      GameBoard.ApplyMove(move);

      // Pause
      Thread.currentThread().sleep(2000);

    } while(move.getType() != GameConstants_AI.RESIGN);

    System.out.println( "Chaturaji.Game_AI Over.  Thanks for playing!" ) ;

    return true;
  }
}
