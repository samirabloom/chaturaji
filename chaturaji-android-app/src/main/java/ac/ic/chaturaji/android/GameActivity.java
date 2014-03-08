package ac.ic.chaturaji.android;

import ac.ic.chaturaji.android.pieces.*;
import ac.ic.chaturaji.chatuService.ChatuService;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.view.*;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

/* Following code done by Kadir Sekha */

public class GameActivity extends Activity {

    private final android.widget.ImageView[][] BoardImage = new android.widget.ImageView[8][8];
    private Pieces[][] Board = new Pieces[8][8];
    private int selected_column = -1; // -1 if nothing selected
    private int selected_row = -1; // -1 if nothing selected
    private boolean[][] valid_moves = new boolean[8][8];
    private boolean moved = false;
    private String movelist = "";
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String colour = getIntent().getStringExtra("colour");
        int identifier = getResources().getIdentifier(colour, "layout", GameActivity.this.getPackageName());
        setContentView(identifier);

        set_pieces();
        set_board();
        draw_pieces();
        play_game();
        set_scoreboard();
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
        savedInstanceState.putSerializable("Board", Board);
        savedInstanceState.putInt("selected_column", selected_column);
        savedInstanceState.putInt("selected_row", selected_row);
        savedInstanceState.putSerializable("valid_moves", valid_moves);
        savedInstanceState.putBoolean("moved", moved);
        savedInstanceState.putString("movelist", movelist);
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
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Board = (Pieces[][]) savedInstanceState.getSerializable("Board");
        selected_column = savedInstanceState.getInt("selected_column");
        selected_row = savedInstanceState.getInt("selected_row");
        valid_moves = (boolean[][]) savedInstanceState.getSerializable("valid_moves");
        moved = savedInstanceState.getBoolean("moved");
        movelist = savedInstanceState.getString("movelist");
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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String colour = getIntent().getStringExtra("colour");
        int identifier = getResources().getIdentifier(colour, "layout", GameActivity.this.getPackageName());
        setContentView(identifier);
        set_board();
        draw_pieces();
        play_game();
        set_scoreboard();

        if((selected_column != -1) && (selected_row != -1) && (!moved))
        {
            select_piece(selected_column, selected_row);
            BoardImage[selected_column][selected_row].setBackgroundColor(getResources().getColor(R.color.light_blue));
            show_valid_moves(selected_column, selected_row);
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

    public void set_pieces() {

        for(int i = 0; i <= 3; i++)
            Board[i][1] = new Pawn(1, (i + 2));

        for(int i = 4; i <= 7; i++)
            Board[1][i] = new Pawn(2, (9 - i));

        for(int i = 4; i <= 7; i++)
            Board[i][6] = new Pawn(3, (9 - i));

        for(int i = 0; i <= 3; i++)
            Board[6][i] = new Pawn(4, (i + 2));

        Board[0][0] = new Boat(1);
        Board[0][7] = new Boat(2);
        Board[7][7] = new Boat(3);
        Board[7][0] = new Boat(4);

        Board[1][0] = new Knight(1);
        Board[0][6] = new Knight(2);
        Board[6][7] = new Knight(3);
        Board[7][1] = new Knight(4);

        Board[2][0] = new Elephant(1);
        Board[0][5] = new Elephant(2);
        Board[5][7] = new Elephant(3);
        Board[7][2] = new Elephant(4);

        Board[3][0] = new King(1);
        Board[0][4] = new King(2);
        Board[4][7] = new King(3);
        Board[7][3] = new King(4);
    }

    public void set_board() {

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                char column_letter = (char)('A' + i);
                char row_number = (char)('1' + j);
                String square = "" + column_letter + row_number;
                int identifier = getResources().getIdentifier(square, "id", GameActivity.this.getPackageName());
                BoardImage[i][j] = (android.widget.ImageView) findViewById(identifier);
            }
    }

    public void draw_pieces() {

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                if(Board[i][j] != null)
                {
                    String colour;
                    if(Board[i][j].colour == 1)
                        colour = "blue";
                    else if(Board[i][j].colour == 2)
                        colour = "red";
                    else if(Board[i][j].colour == 3)
                        colour = "green";
                    else if(Board[i][j].colour == 4)
                        colour = "yellow";
                    else
                        colour = "";

                    String piece_type;

                    if(Board[i][j] instanceof Pawn)
                        piece_type = "pawn";
                    else if(Board[i][j] instanceof Boat)
                        piece_type = "boat";
                    else if(Board[i][j] instanceof Knight)
                        piece_type = "knight";
                    else if(Board[i][j] instanceof Elephant)
                        piece_type = "elephant";
                    else if(Board[i][j] instanceof King)
                        piece_type = "king";
                    else
                        piece_type = "";

                    String piece = colour + piece_type;
                    int identifier = getResources().getIdentifier(piece, "drawable", GameActivity.this.getPackageName());
                    BoardImage[i][j].setImageResource(identifier);
                }
                else
                    BoardImage[i][j].setImageResource(0);
            }
    }

    public void play_game() {

        int i;
        int j;

        for(i = 0; i < 8; i++)
        {
            final int column = i;
            for(j = 0; j < 8; j++)
            {
                final int row = j;
                BoardImage[column][row].setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        if(selected_column == column && selected_row == row)
                            clear_selections();
                        else if ((selected_column != -1) && (selected_row != -1) && valid_moves[column][row])
                        {
                            move(selected_column, selected_row, column, row);
                            moved = true;
                            move_count++;
                            pawn_promotion();

                            while(!check_valid_moves())
                                move_count++;

                            set_scoreboard();
                            clear_selections();
                            draw_pieces();
                        }
                        else if ((!moved) && select_piece(column, row))
                        {
                            clear_selections();
                            BoardImage[column][row].setBackgroundColor(getResources().getColor(R.color.light_blue));
                            selected_column = column;
                            selected_row = row;
                            show_valid_moves(column, row);
                        }

                        moved = false;
                    }
                });
            }
        }
    }

    public void set_scoreboard() {

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
        move_list.setText(movelist);

        TextView show_turn = (TextView) findViewById(R.id.turn);
        int turn = (move_count % 4) + 1;
        String colour;

        if(turn == 1)
            colour = "Blue";
        else if(turn == 2)
            colour = "Red";
        else if(turn == 3)
            colour = "Green";
        else if(turn == 4)
            colour = "Yellow";
        else
            colour = "";

        String turn_string = "It is " + colour + "'s turn to move";
        show_turn.setText(turn_string);
    }

    public void adjust_scoreboard(int source_column, int source_row, int destination_column, int destination_row) {

        int score = 0;
        boolean boattriumph = false;
        String taken_type = "";
        String taken_colour = "";

        if(Board[destination_column][destination_row] != null)
        {
            if(Board[destination_column][destination_row] instanceof Pawn)
            {
                score = 1;
                taken_type = "Pawn";
            }
            else if(Board[destination_column][destination_row] instanceof Boat)
            {
                score = 2;
                taken_type = "Boat";
            }
            else if(Board[destination_column][destination_row] instanceof Knight)
            {
                score = 3;
                taken_type = "Knight";
            }
            else if(Board[destination_column][destination_row] instanceof Elephant)
            {
                score = 4;
                taken_type = "Elephant";
            }
            else if(Board[destination_column][destination_row] instanceof King)
            {
                score = 5;
                taken_type = "King";
                if(Board[destination_column][destination_row].colour == 1)
                    blue_king_captured_by = Board[source_column][source_row].colour;
                else if(Board[destination_column][destination_row].colour == 2)
                    red_king_captured_by = Board[source_column][source_row].colour;
                else if(Board[destination_column][destination_row].colour == 3)
                    green_king_captured_by = Board[source_column][source_row].colour;
                else if(Board[destination_column][destination_row].colour == 4)
                    yellow_king_captured_by = Board[source_column][source_row].colour;

                if(blue_king_captured_by == 0 && red_king_captured_by == 1 && green_king_captured_by == 1 && yellow_king_captured_by == 1)
                    score = score + 54;
                else if(blue_king_captured_by == 2 && red_king_captured_by == 0 &&green_king_captured_by == 2 && yellow_king_captured_by == 2)
                    score = score + 54;
                else if(blue_king_captured_by == 3 && red_king_captured_by == 3 && green_king_captured_by == 0 &&yellow_king_captured_by == 3)
                    score = score + 54;
                else if(blue_king_captured_by == 4 && red_king_captured_by == 4 && green_king_captured_by == 4 && yellow_king_captured_by == 0)
                    score = score + 54;
            }

            if(Board[destination_column][destination_row].colour == 1)
                taken_colour = "Blue";
            else if(Board[destination_column][destination_row].colour == 2)
                taken_colour = "Red";
            else if(Board[destination_column][destination_row].colour == 3)
                taken_colour = "Green";
            else if(Board[destination_column][destination_row].colour == 4)
                taken_colour = "Yellow";
            else
                taken_colour = "";
        }

        if(Board[source_column][source_row] instanceof Boat)
            if(boat_triumph(destination_column, destination_row))
            {
                score = score + 6;
                boattriumph = true;
            }

        if(Board[source_column][source_row].colour == 1)
            blue_score = blue_score + score;
        else if(Board[source_column][source_row].colour == 2)
            red_score = red_score + score;
        else if(Board[source_column][source_row].colour == 3)
            green_score = green_score + score;
        else if(Board[source_column][source_row].colour == 4)
            yellow_score = yellow_score + score;

        if(!boattriumph && Board[destination_column][destination_row] == null)
            movelist = movelist + (move_count + 1) + ". " + (char)(source_column + 65) + (source_row + 1) + " to " + (char)(destination_column + 65) + (destination_row + 1) + "\n";
        else if(boattriumph)
            movelist = movelist + (move_count + 1) + ". " + (char)(source_column + 65) + (source_row + 1) + " to " + (char)(destination_column + 65) + (destination_row + 1) + " Boat Triumph!\n";
        else if(Board[destination_column][destination_row] != null)
            movelist = movelist + (move_count + 1) + ". " + (char)(source_column + 65) + (source_row + 1) + " to " + (char)(destination_column + 65) + (destination_row + 1) + " taking " + taken_colour + "'s " + taken_type + "\n";
    }

    public boolean select_piece(int column, int row) {

        int turn = (move_count % 4) + 1;
        return ((Board[column][row] != null) && (Board[column][row].colour == turn));
    }

    public void clear_selections() {

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                BoardImage[i][j].setBackgroundColor(getResources().getColor(R.color.transparent));
                valid_moves[i][j] = false;
            }

        selected_row = -1;
        selected_column = -1;
    }

    public boolean check_valid_moves() {

        int turn = (move_count % 4) + 1;
        boolean[][] check_moves;

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                if(Board[i][j] != null && Board[i][j].colour == turn)
                {
                    check_moves = Board[i][j].valid_moves(i, j, Board);
                    for(int x = 0; x < 8; x++)
                        for(int y = 0; y < 8; y++)
                            if(check_moves[x][y])
                                return true;
                }

        return false;
    }

    public void show_valid_moves(int column, int row) {

        valid_moves = Board[column][row].valid_moves(column, row, Board);

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                if(valid_moves[i][j])
                    BoardImage[i][j].setBackgroundColor(getResources().getColor(R.color.light_blue));

    }

    public void move(int source_column, int source_row, int destination_column, int destination_row) {

        adjust_scoreboard(source_column, source_row, destination_column, destination_row);

        Board[destination_column][destination_row] = Board[source_column][source_row];
        Board[source_column][source_row] = null;
    }

    public boolean check_promotion(int column, int row, int piece_type, int colour) {

        if(colour == 1 && row != 7)
            return false;
        else if(colour == 2 && column != 7)
            return false;
        else if(colour == 3 && row != 0)
            return false;
        else if(colour == 4 && column != 0)
            return false;

        if(piece_type == 2)
            for(int i = 0; i < 8; i++)
                for(int j = 0; j < 8; j++)
                {
                    if((Board[i][j] instanceof Boat) && (Board[i][j].colour == colour))
                        return false;
                }
        else if(piece_type == 3)
            for(int i = 0; i < 8; i++)
                for(int j = 0; j < 8; j++)
                {
                    if((Board[i][j] instanceof Knight) && (Board[i][j].colour == colour))
                        return false;
                }
        else if(piece_type == 4)
            for(int i = 0; i < 8; i++)
                for(int j = 0; j < 8; j++)
                {
                    if((Board[i][j] instanceof Elephant) && (Board[i][j].colour == colour))
                        return false;
                }
        else if(piece_type == 5)
            for(int i = 0; i < 8; i++)
                for(int j = 0; j < 8; j++)
                {
                    if((Board[i][j] instanceof King) && (Board[i][j].colour == colour))
                        return false;
                }

        return true;
    }

    public void pawn_promotion() {

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                if(i == 0 || j == 0 || i == 7 || j == 7)
                {
                    if(Board[i][j] instanceof Pawn)
                    {
                        if(check_promotion(i, j, Board[i][j].promotion, Board[i][j].colour))
                        {
                            if(Board[i][j].promotion == 2)
                                Board[i][j] = new Boat(Board[i][j].colour);
                            else if(Board[i][j].promotion == 3)
                                Board[i][j] = new Knight(Board[i][j].colour);
                            else if(Board[i][j].promotion == 4)
                                Board[i][j] = new Elephant(Board[i][j].colour);
                            else if(Board[i][j].promotion == 5)
                            {
                                Board[i][j] = new King(Board[i][j].colour);
                                if(Board[i][j].colour == 1)
                                    blue_king_captured_by = 0;
                                else if(Board[i][j].colour == 2)
                                    red_king_captured_by = 0;
                                else if(Board[i][j].colour == 3)
                                    green_king_captured_by = 0;
                                else if(Board[i][j].colour == 4)
                                    yellow_king_captured_by = 0;
                            }
                        }
                    }
                }
    }

    public boolean boat_triumph(int column, int row) {

        if(column <= 6 && row <= 6 && Board[column + 1][row] instanceof Boat && Board[column + 1][row + 1] instanceof Boat && Board[column][row + 1] instanceof Boat)
        {
            Board[column + 1][row] = null;
            Board[column + 1][row + 1] = null;
            Board[column][row + 1] = null;
            return true;
        }

        else if(column <= 6 && row >= 1 && Board[column + 1][row] instanceof Boat && Board[column + 1][row - 1] instanceof Boat && Board[column][row - 1] instanceof Boat)
        {
            Board[column + 1][row] = null;
            Board[column + 1][row - 1] = null;
            Board[column][row - 1] = null;
            return true;
        }

        else if(column >= 1 && row <= 6 && Board[column][row + 1] instanceof Boat && Board[column - 1][row] instanceof Boat && Board[column - 1][row + 1] instanceof Boat)
        {
            Board[column][row + 1] = null;
            Board[column - 1][row] = null;
            Board[column - 1][row + 1] = null;
            return true;
        }

        else if(column >= 1 && row >= 1 && Board[column - 1][row] instanceof Boat && Board[column - 1][row - 1] instanceof Boat && Board[column][row - 1] instanceof Boat)
        {
            Board[column - 1][row] = null;
            Board[column - 1][row - 1] = null;
            Board[column][row - 1] = null;
            return true;
        }

        return false;
    }
}
