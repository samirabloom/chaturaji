package ac.ic.chaturaji.android;

import ac.ic.chaturaji.android.pieces.*;
import ac.ic.chaturaji.chatuService.ChaturajiService;
import ac.ic.chaturaji.chatuService.OnMoveCompleteListener;
import ac.ic.chaturaji.model.GameStatus;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.Result;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/* Following code done by Kadir Sekha */

public class GameActivity extends Activity implements OnMoveCompleteListener {

    private static final String TAG = "GameActivity";
    private final android.widget.ImageView[][] BoardImage = new android.widget.ImageView[8][8];
    private Pieces[][] board = new Pieces[8][8];
    private int player_colour;
    private int selected_column = -1; // -1 if nothing selected
    private int selected_row = -1; // -1 if nothing selected
    private boolean[][] valid_moves = new boolean[8][8];
    private boolean moved = false;
    private String moveList = "";
    private String numberOfAIs = "0";
    private int blue_score = 0;
    private int red_score = 0;
    private int green_score = 0;
    private int yellow_score = 0;
    private int move_count = 0;
    private int blue_king_captured_by = 0;
    private int red_king_captured_by = 0;
    private int green_king_captured_by = 0;
    private int yellow_king_captured_by = 0;
    private boolean gameInPlay = true;
    private boolean replay = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ChaturajiService chaturajiService = ChaturajiService.getInstance();
        chaturajiService.setupSocketClient(GameActivity.this);

        String colour = getIntent().getStringExtra("colour");
        boolean Replay = getIntent().getBooleanExtra("Replay?", false);

        switch (colour) {
            case "in_game_yellow":
                player_colour = 4;
                break;
            case "in_game_blue":
                player_colour = 1;
                break;
            case "in_game_red":
                player_colour = 2;
                break;
            case "in_game_green":
                player_colour = 3;
                break;
        }

        int identifier = getResources().getIdentifier(colour, "layout", GameActivity.this.getPackageName());
        setContentView(identifier);

        setPieces();
        setBoard();
        drawPieces();
        playGame();
        setScoreboard();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("board", board);
        savedInstanceState.putInt("selected_column", selected_column);
        savedInstanceState.putInt("selected_row", selected_row);
        savedInstanceState.putSerializable("valid_moves", valid_moves);
        savedInstanceState.putBoolean("moved", moved);
        savedInstanceState.putString("moveList", moveList);
        savedInstanceState.putString("numberOfAIs", numberOfAIs);
        savedInstanceState.putInt("blue_score", blue_score);
        savedInstanceState.putInt("red_score", red_score);
        savedInstanceState.putInt("green_score", green_score);
        savedInstanceState.putInt("yellow_score", yellow_score);
        savedInstanceState.putInt("move_count", move_count);
        savedInstanceState.putInt("blue_king_captured_by", blue_king_captured_by);
        savedInstanceState.putInt("red_king_captured_by", red_king_captured_by);
        savedInstanceState.putInt("green_king_captured_by", green_king_captured_by);
        savedInstanceState.putInt("yellow_king_captured_by", yellow_king_captured_by);
        savedInstanceState.putBoolean("replay", replay);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        board = (Pieces[][]) savedInstanceState.getSerializable("board");
        selected_column = savedInstanceState.getInt("selected_column");
        selected_row = savedInstanceState.getInt("selected_row");
        valid_moves = (boolean[][]) savedInstanceState.getSerializable("valid_moves");
        moved = savedInstanceState.getBoolean("moved");
        moveList = savedInstanceState.getString("moveList");
        numberOfAIs = savedInstanceState.getString("numberOfAIs");
        blue_score = savedInstanceState.getInt("blue_score");
        red_score = savedInstanceState.getInt("red_score");
        green_score = savedInstanceState.getInt("green_score");
        yellow_score = savedInstanceState.getInt("yellow_score");
        move_count = savedInstanceState.getInt("move_count");
        blue_king_captured_by = savedInstanceState.getInt("blue_king_captured_by");
        red_king_captured_by = savedInstanceState.getInt("red_king_captured_by");
        green_king_captured_by = savedInstanceState.getInt("green_king_captured_by");
        yellow_king_captured_by = savedInstanceState.getInt("yellow_king_captured_by");
        replay = savedInstanceState.getBoolean("replay");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String colour = getIntent().getStringExtra("colour");
        int identifier = getResources().getIdentifier(colour, "layout", GameActivity.this.getPackageName());
        setContentView(identifier);
        setBoard();
        drawPieces();
        playGame();
        setScoreboard();

        if ((selected_column != -1) && (selected_row != -1) && (!moved)) {
            selectPiece(selected_column, selected_row);
            BoardImage[selected_column][selected_row].setBackgroundColor(getResources().getColor(R.color.light_blue));
            showValidMoves(selected_column, selected_row);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setPieces() {

        for (int i = 4; i <= 7; i++) {
            board[i][1] = new Pawn(1, (9 - i));
        }
        for (int i = 4; i <= 7; i++) {
            board[6][i] = new Pawn(2, (9 - i));
        }
        for (int i = 0; i <= 3; i++) {
            board[i][6] = new Pawn(3, (i + 2));
        }
        for (int i = 0; i <= 3; i++) {
            board[1][i] = new Pawn(4, (i + 2));
        }

        board[7][0] = new Boat(1);
        board[7][7] = new Boat(2);
        board[0][7] = new Boat(3);
        board[0][0] = new Boat(4);

        board[6][0] = new Knight(1);
        board[7][6] = new Knight(2);
        board[1][7] = new Knight(3);
        board[0][1] = new Knight(4);

        board[5][0] = new Elephant(1);
        board[7][5] = new Elephant(2);
        board[2][7] = new Elephant(3);
        board[0][2] = new Elephant(4);

        board[4][0] = new King(1);
        board[7][4] = new King(2);
        board[3][7] = new King(3);
        board[0][3] = new King(4);
    }

    public void setBoard() {

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                char column_letter = (char) ('A' + i);
                char row_number = (char) ('1' + j);
                String square = "" + column_letter + row_number;
                int identifier = getResources().getIdentifier(square, "id", GameActivity.this.getPackageName());
                BoardImage[i][j] = (android.widget.ImageView) findViewById(identifier);
            }
    }

    public void drawPieces() {

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != null) {
                    String colour;
                    if (board[i][j].colour == 1)
                        colour = "blue";
                    else if (board[i][j].colour == 2)
                        colour = "red";
                    else if (board[i][j].colour == 3)
                        colour = "green";
                    else if (board[i][j].colour == 4)
                        colour = "yellow";
                    else
                        colour = "";

                    String piece_type;

                    if (board[i][j] instanceof Pawn)
                        piece_type = "pawn";
                    else if (board[i][j] instanceof Boat)
                        piece_type = "boat";
                    else if (board[i][j] instanceof Knight)
                        piece_type = "knight";
                    else if (board[i][j] instanceof Elephant)
                        piece_type = "elephant";
                    else if (board[i][j] instanceof King)
                        piece_type = "king";
                    else
                        piece_type = "";

                    String piece = colour + piece_type;
                    int identifier = getResources().getIdentifier(piece, "drawable", GameActivity.this.getPackageName());
                    BoardImage[i][j].setImageResource(identifier);
                } else
                    BoardImage[i][j].setImageResource(0);
            }
    }

    public void playGame() {

        int i;
        int j;
        if (!replay) {
            for (i = 0; i < 8; i++) {
                final int column = i;
                for (j = 0; j < 8; j++) {
                    final int row = j;
                    BoardImage[column][row].setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {

                            if (gameInPlay) {
                                if ((((move_count + 3) % 4) + 1) == player_colour) {
                                    if (selected_column == column && selected_row == row) {
                                        clearSelections();
                                    } else if ((selected_column != -1) && (selected_row != -1) && valid_moves[column][row]) {
                                        try {
                                            String state = new SubmitMove().execute(convertMove(selected_column, selected_row), convertMove(column, row)).get();
                                            switch (state) {
                                                case "Error":
                                                    Toast.makeText(getApplicationContext(), "Sorry there was a problem connecting with the server", Toast.LENGTH_LONG).show();
                                                    break;
                                                case "Success":
                                                    break;
                                                default:
                                                    Toast.makeText(getApplicationContext(), state, Toast.LENGTH_LONG).show();
                                                    break;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else if ((!moved) && selectPiece(column, row)) {
                                        clearSelections();
                                        BoardImage[column][row].setBackgroundColor(getResources().getColor(R.color.light_blue));
                                        selected_column = column;
                                        selected_row = row;
                                        showValidMoves(column, row);
                                    }

                                    moved = false;
                                }
                            }
                        }
                    });
                }
            }
        }
    }

    public void setScoreboard() {

        TextView blue_score_text = (TextView) findViewById(R.id.blue_score);
        TextView red_score_text = (TextView) findViewById(R.id.red_score);
        TextView green_score_text = (TextView) findViewById(R.id.green_score);
        TextView yellow_score_text = (TextView) findViewById(R.id.yellow_score);
        TextView move_list = (TextView) findViewById(R.id.movelist);

        String blue = "Blue Score: " + blue_score;
        blue_score_text.setText(blue);

        String red = "Red Score: " + red_score;
        red_score_text.setText(red);

        String green = "Green Score: " + green_score;
        green_score_text.setText(green);

        String yellow = "Yellow Score: " + yellow_score;
        yellow_score_text.setText(yellow);

        move_list.setMovementMethod(new ScrollingMovementMethod());
        move_list.setText(moveList);

        TextView show_turn = (TextView) findViewById(R.id.turn);
        int gameOver = checkEndGame();

        if (gameInPlay) {
            if (gameOver != 0) {
                String colour;

                if (gameOver == 1)
                    colour = "Blue";
                else if (gameOver == 2)
                    colour = "Red";
                else if (gameOver == 3)
                    colour = "Green";
                else if (gameOver == 4)
                    colour = "Yellow";
                else
                    colour = "";

                if (gameOver == 5)
                    show_turn.setText("It is a draw!");
                else {
                    String winner = colour + " wins!";
                    show_turn.setText(winner);
                }

            } else {
                int turn = ((move_count + 3) % 4) + 1;
                String colour;

                if (turn == 1)
                    colour = "Blue";
                else if (turn == 2)
                    colour = "Red";
                else if (turn == 3)
                    colour = "Green";
                else if (turn == 4)
                    colour = "Yellow";
                else
                    colour = "";


                String turn_string = "It is " + colour + "'s turn to move";
                show_turn.setText(turn_string);
            }
        }
    }

    public void adjustScoreboard(int source_column, int source_row, int destination_column, int destination_row) {

        int score = 0;
        boolean boattriumph = false;
        String taken_type = "";
        String taken_colour = "";

        if (board[destination_column][destination_row] != null) {
            if (board[destination_column][destination_row] instanceof Pawn) {
                score = 1;
                taken_type = "Pawn";
            } else if (board[destination_column][destination_row] instanceof Boat) {
                score = 2;
                taken_type = "Boat";
            } else if (board[destination_column][destination_row] instanceof Knight) {
                score = 3;
                taken_type = "Knight";
            } else if (board[destination_column][destination_row] instanceof Elephant) {
                score = 4;
                taken_type = "Elephant";
            } else if (board[destination_column][destination_row] instanceof King) {
                score = 5;
                taken_type = "King";
                if (board[source_column][source_row] != null) {
                    if (board[destination_column][destination_row].colour == 1) {
                        blue_king_captured_by = board[source_column][source_row].colour;
                    } else if (board[destination_column][destination_row].colour == 2) {
                        red_king_captured_by = board[source_column][source_row].colour;
                    } else if (board[destination_column][destination_row].colour == 3) {
                        green_king_captured_by = board[source_column][source_row].colour;
                    } else if (board[destination_column][destination_row].colour == 4) {
                        yellow_king_captured_by = board[source_column][source_row].colour;
                    }
                }

                if (blue_king_captured_by == 0 && red_king_captured_by == 1 && green_king_captured_by == 1 && yellow_king_captured_by == 1) {
                    score = score + 54;
                } else if (blue_king_captured_by == 2 && red_king_captured_by == 0 && green_king_captured_by == 2 && yellow_king_captured_by == 2) {
                    score = score + 54;
                } else if (blue_king_captured_by == 3 && red_king_captured_by == 3 && green_king_captured_by == 0 && yellow_king_captured_by == 3) {
                    score = score + 54;
                } else if (blue_king_captured_by == 4 && red_king_captured_by == 4 && green_king_captured_by == 4 && yellow_king_captured_by == 0) {
                    score = score + 54;
                }
            }

            if (board[destination_column][destination_row].colour == 1)
                taken_colour = "Blue";
            else if (board[destination_column][destination_row].colour == 2)
                taken_colour = "Red";
            else if (board[destination_column][destination_row].colour == 3)
                taken_colour = "Green";
            else if (board[destination_column][destination_row].colour == 4)
                taken_colour = "Yellow";
            else
                taken_colour = "";
        }

        if (board[source_column][source_row] instanceof Boat)
            if (boatTriumph(destination_column, destination_row)) {
                score = score + 6;
                boattriumph = true;
            }

        if (board[source_column][source_row] != null) {
            if (board[source_column][source_row].colour == 1) {
                blue_score = blue_score + score;
            } else if (board[source_column][source_row].colour == 2) {
                red_score = red_score + score;
            } else if (board[source_column][source_row].colour == 3) {
                green_score = green_score + score;
            } else if (board[source_column][source_row].colour == 4) {
                yellow_score = yellow_score + score;
            }
        }

        if (!boattriumph && board[destination_column][destination_row] == null)
            moveList = (move_count + 1) + ". " + (char) (source_column + 65) + (source_row + 1) + " to " + (char) (destination_column + 65) + (destination_row + 1) + "\n" + moveList;
        else if (boattriumph)
            moveList = (move_count + 1) + ". " + (char) (source_column + 65) + (source_row + 1) + " to " + (char) (destination_column + 65) + (destination_row + 1) + " Boat Triumph!\n" + moveList;
        else if (board[destination_column][destination_row] != null)
            moveList = (move_count + 1) + ". " + (char) (source_column + 65) + (source_row + 1) + " to " + (char) (destination_column + 65) + (destination_row + 1) + " taking " + taken_colour + "'s " + taken_type + "\n" + moveList;
    }

    public boolean selectPiece(int column, int row) {

        int turn = ((move_count + 3) % 4) + 1;
        return ((board[column][row] != null) && (board[column][row].colour == turn));
    }

    public void clearSelections() {

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++) {
                BoardImage[i][j].setBackgroundColor(getResources().getColor(R.color.transparent));
                valid_moves[i][j] = false;
            }

        selected_row = -1;
        selected_column = -1;
    }

    public boolean checkValidMoves() {

        int turn = ((move_count + 3) % 4) + 1;
        boolean[][] check_moves;

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (board[i][j] != null && board[i][j].colour == turn) {
                    check_moves = board[i][j].valid_moves(i, j, board);
                    for (int x = 0; x < 8; x++)
                        for (int y = 0; y < 8; y++)
                            if (check_moves[x][y])
                                return true;
                }

        return false;
    }

    public int checkEndGame() {

        int colour = 0;

        for (int i = 0; i < 8; i++) {
            if (colour != 0)
                break;

            for (int j = 0; j < 8; j++)
                if (board[i][j] != null) {
                    colour = board[i][j].colour;
                    break;
                }
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((board[i][j] != null) && (board[i][j].colour != colour))
                    return 0;
            }
        }

        int final_score = 0;

        if (blue_score >= final_score)
            final_score = blue_score;

        if (red_score >= final_score)
            final_score = red_score;

        if (green_score >= final_score)
            final_score = green_score;

        if (yellow_score >= final_score)
            final_score = yellow_score;

        if (final_score == blue_score) {
            if (blue_score == red_score || blue_score == green_score || blue_score == yellow_score)
                colour = 5;
            else
                colour = 1;
        } else if (final_score == red_score) {
            if (red_score == green_score || red_score == yellow_score)
                colour = 5;
            else
                colour = 2;
        } else if (final_score == green_score) {
            if (green_score == yellow_score)
                colour = 5;
            else
                colour = 3;
        } else if (final_score == yellow_score)
            colour = 4;

        return colour;
    }

    public void showValidMoves(int column, int row) {

        valid_moves = board[column][row].valid_moves(column, row, board);

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (valid_moves[i][j])
                    BoardImage[i][j].setBackgroundColor(getResources().getColor(R.color.light_blue));

    }

    public void move(int source_column, int source_row, int destination_column, int destination_row) {

        adjustScoreboard(source_column, source_row, destination_column, destination_row);

        board[destination_column][destination_row] = board[source_column][source_row];
        board[source_column][source_row] = null;
    }

    public boolean checkPromotion(int column, int row, int piece_type, int colour) {

        if (colour == 1 && row != 7)
            return false;
        else if (colour == 2 && column != 7)
            return false;
        else if (colour == 3 && row != 0)
            return false;
        else if (colour == 4 && column != 0)
            return false;

        if (piece_type == 2)
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++) {
                    if ((board[i][j] instanceof Boat) && (board[i][j].colour == colour))
                        return false;
                }
        else if (piece_type == 3)
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++) {
                    if ((board[i][j] instanceof Knight) && (board[i][j].colour == colour))
                        return false;
                }
        else if (piece_type == 4)
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++) {
                    if ((board[i][j] instanceof Elephant) && (board[i][j].colour == colour))
                        return false;
                }
        else if (piece_type == 5)
            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 8; j++) {
                    if ((board[i][j] instanceof King) && (board[i][j].colour == colour))
                        return false;
                }

        return true;
    }

    public void pawnPromotion() {

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (i == 0 || j == 0 || i == 7 || j == 7) {
                    if (board[i][j] instanceof Pawn) {
                        if (checkPromotion(i, j, board[i][j].promotion, board[i][j].colour)) {
                            if (board[i][j].promotion == 2)
                                board[i][j] = new Boat(board[i][j].colour);
                            else if (board[i][j].promotion == 3)
                                board[i][j] = new Knight(board[i][j].colour);
                            else if (board[i][j].promotion == 4)
                                board[i][j] = new Elephant(board[i][j].colour);
                            else if (board[i][j].promotion == 5) {
                                board[i][j] = new King(board[i][j].colour);
                                if (board[i][j].colour == 1)
                                    blue_king_captured_by = 0;
                                else if (board[i][j].colour == 2)
                                    red_king_captured_by = 0;
                                else if (board[i][j].colour == 3)
                                    green_king_captured_by = 0;
                                else if (board[i][j].colour == 4)
                                    yellow_king_captured_by = 0;
                            }
                        }
                    }
                }
    }

    public boolean boatTriumph(int column, int row) {

        if (column <= 6 && row <= 6 && board[column + 1][row] instanceof Boat && board[column + 1][row + 1] instanceof Boat && board[column][row + 1] instanceof Boat) {
            board[column + 1][row] = null;
            board[column + 1][row + 1] = null;
            board[column][row + 1] = null;
            return true;
        } else if (column <= 6 && row >= 1 && board[column + 1][row] instanceof Boat && board[column + 1][row - 1] instanceof Boat && board[column][row - 1] instanceof Boat) {
            board[column + 1][row] = null;
            board[column + 1][row - 1] = null;
            board[column][row - 1] = null;
            return true;
        } else if (column >= 1 && row <= 6 && board[column][row + 1] instanceof Boat && board[column - 1][row] instanceof Boat && board[column - 1][row + 1] instanceof Boat) {
            board[column][row + 1] = null;
            board[column - 1][row] = null;
            board[column - 1][row + 1] = null;
            return true;
        } else if (column >= 1 && row >= 1 && board[column - 1][row] instanceof Boat && board[column - 1][row - 1] instanceof Boat && board[column][row - 1] instanceof Boat) {
            board[column - 1][row] = null;
            board[column - 1][row - 1] = null;
            board[column][row - 1] = null;
            return true;
        }

        return false;
    }

    public int convertMove(int column, int row) {

        return (column + (row * 8));
    }

    public int getColumn(int move) {
        return (move % 8);
    }

    public int getRow(int move) {
        return (move / 8);
    }

    // This method gets called by the server every time a new move is made, including your current player's move

    public void updateGame(Result result) {

        int colSrc = getColumn(result.getMove().getSource());
        int rowSrc = getRow(result.getMove().getSource());

        int colDest = getColumn(result.getMove().getDestination());
        int rowDest = getRow(result.getMove().getDestination());

        move(colSrc, rowSrc, colDest, rowDest);
        moved = true;
        move_count++;

        String winnerMessage = "";
        if (result.getGameStatus() != null) {
            switch (result.getGameStatus()) {
                case GAME_OVER:
                    List<Player> playersWithHighestScore = result.getGame().getPlayersWithHighestScore();
                    for (int i = 0; i < playersWithHighestScore.size(); i++) {
                        winnerMessage += playersWithHighestScore.get(i).getColour().name();
                        if (i < playersWithHighestScore.size() - 1) {
                            winnerMessage += " and ";
                        }
                    }
                    if (winnerMessage.length() > 0) {
                        winnerMessage += " wins";
                    }
                    moveList = "-- GAME OVER --\n" + moveList;
                    if (winnerMessage.length() > 0) {
                        moveList = winnerMessage + "\n" + moveList;
                    }
                    gameInPlay = false;
                    break;
                case STALEMATE:
                    moveList = "-- STALEMATE --\n" + moveList;
                    gameInPlay = false;
                    break;
            }
        }

        updateGameThread(result.getGameStatus(), winnerMessage);
    }

    // this makes sure the server callback updates happen on the main UI thread

    private void updateGameThread(final GameStatus gameStatus, final String winnersMessage) {

        new Thread() {
            public void run() {
                try {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            pawnPromotion();

                            while (!checkValidMoves()) {
                                move_count++;
                            }

                            setScoreboard();
                            clearSelections();
                            drawPieces();

                            if (gameStatus != null) {
                                switch (gameStatus) {
                                    case GAME_OVER: {
                                        Toast.makeText(getApplicationContext(), "-- GAME OVER " + (winnersMessage.length() > 0 ? winnersMessage : "") + "--", Toast.LENGTH_LONG).show();
                                        ((TextView) findViewById(R.id.turn)).setText(winnersMessage);
                                        break;
                                    }
                                    case STALEMATE: {
                                        Toast.makeText(getApplicationContext(), "-- STALEMATE --", Toast.LENGTH_LONG).show();
                                        ((TextView) findViewById(R.id.turn)).setText("-- STALEMATE --");
                                        break;
                                    }
                                }
                            }

                            findViewById(R.id.board_layout).invalidate();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    // This is for making a submit move request to the server, you just need to pass in the source and destination of the move

    public ImageView[][] getBoardImage() {
        return BoardImage;
    }

    /* Get functions for testing */

    public Pieces[][] getBoard() {
        return board;
    }

    public int getSelected_column() {
        return selected_column;
    }

    public int getSelected_row() {
        return selected_row;
    }

    public int getBlue_score() {
        return blue_score;
    }

    public int getRed_score() {
        return red_score;
    }

    public int getGreen_score() {
        return green_score;
    }

    public int getYellow_score() {
        return yellow_score;
    }

    public int getMove_count() {
        return move_count;
    }

    public int getBlue_king_captured_by() {
        return blue_king_captured_by;
    }

    public int getRed_king_captured_by() {
        return red_king_captured_by;
    }

    public int getGreen_king_captured_by() {
        return green_king_captured_by;
    }

    public int getYellow_king_captured_by() {
        return yellow_king_captured_by;
    }

    public boolean[][] getValid_moves() {
        return valid_moves;
    }

    public boolean getMoved() {
        return moved;
    }

    public String getMoveList() {
        return moveList;
    }

    private class SubmitMove extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... info) {
            return ChaturajiService.getInstance().submitMove(info[0], info[1]);
        }
    }
}
