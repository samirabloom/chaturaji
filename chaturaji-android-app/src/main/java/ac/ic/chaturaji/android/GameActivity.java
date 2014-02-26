package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChatuService;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.*;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

/* Following code done by Kadir Sekha */

public class GameActivity extends Activity {

    private final android.widget.ImageView[][] Board = new android.widget.ImageView[8][8];
    private int[][] pieces_colour = new int[8][8];
    private int[][] pieces_type = new int[8][8];
    private int selected_column = -1; // -1 if nothing selected
    private int selected_row = -1; // -1 if nothing selected
    private boolean[][] valid_moves = new boolean[8][8];
    private boolean moved = false;
    private String numberOfAIs = "0";

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
        draw_pieces();
        play_game();
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
        savedInstanceState.putSerializable("pieces_colour", pieces_colour);
        savedInstanceState.putSerializable("pieces_type", pieces_type);
        savedInstanceState.putInt("selected_column", selected_column);
        savedInstanceState.putInt("selected_row", selected_row);
        savedInstanceState.putSerializable("valid_moves", valid_moves);
        savedInstanceState.putBoolean("moved", moved);
        savedInstanceState.putString("numberOfAIs", numberOfAIs);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pieces_colour = (int[][]) savedInstanceState.getSerializable("pieces_colour");
        pieces_type = (int[][]) savedInstanceState.getSerializable("pieces_type");
        selected_column = savedInstanceState.getInt("selected_column");
        selected_row = savedInstanceState.getInt("selected_row");
        valid_moves = (boolean[][]) savedInstanceState.getSerializable("valid_moves");
        moved = savedInstanceState.getBoolean("moved");
        numberOfAIs = savedInstanceState.getString("numberOfAIs");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        String colour = getIntent().getStringExtra("colour");
        int identifier = getResources().getIdentifier(colour, "layout", GameActivity.this.getPackageName());
        setContentView(identifier);
        draw_pieces();
        play_game();

        if((selected_column != -1) && (selected_row != -1) && (!moved))
        {
            select_piece(selected_column, selected_row);
            show_valid_moves(selected_column, selected_row);
        }
    }

    public void set_pieces() {

        //pieces_colour: 0 if empty, 1 if blue, 2 if red, 3 if green, 4 if yellow
        //pieces_type: 0 if empty 1 if pawn, 2 if ship, 3 if knight, 4 if elephant, 5 if king
        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                pieces_colour[i][j] = 0;
                pieces_type[i][j] = 0;
            }

        for(int i = 0; i <= 3; i++)
            for(int j = 0; j <= 1; j++)
            {
                pieces_colour[i][j] = 1;

                if(j == 1)
                    pieces_type[i][j] = 1;
            }

        for(int i = 0; i <= 1; i++)
            for(int j = 4; j <= 7; j++)
            {
                pieces_colour[i][j] = 2;

                if(i == 1)
                    pieces_type[i][j] = 1;
            }

        for(int i = 4; i <= 7; i++)
            for(int j = 6; j <= 7; j++)
            {
                pieces_colour[i][j] = 3;

                if(j == 6)
                    pieces_type[i][j] = 1;
            }

        for(int i = 6; i <= 7; i++)
            for(int j = 0; j <= 3; j++)
            {
                pieces_colour[i][j] = 4;

                if(i == 6)
                    pieces_type[i][j] = 1;
            }

        pieces_type[0][0] = 2;
        pieces_type[0][7] = 2;
        pieces_type[7][7] = 2;
        pieces_type[7][0] = 2;

        pieces_type[1][0] = 3;
        pieces_type[0][6] = 3;
        pieces_type[6][7] = 3;
        pieces_type[7][1] = 3;

        pieces_type[2][0] = 4;
        pieces_type[0][5] = 4;
        pieces_type[5][7] = 4;
        pieces_type[7][2] = 4;

        pieces_type[3][0] = 5;
        pieces_type[0][4] = 5;
        pieces_type[4][7] = 5;
        pieces_type[7][3] = 5;

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                valid_moves[i][j] = false;
    }

    public void draw_pieces() {

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                char column_letter = (char)('A' + i);
                char row_number = (char)('1' + j);
                String square = "" + column_letter + row_number;
                int identifier = getResources().getIdentifier(square, "id", GameActivity.this.getPackageName());
                Board[i][j] = (android.widget.ImageView) findViewById(identifier);
            }

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                if(pieces_colour[i][j] != 0)
                {
                    String colour;
                    if(pieces_colour[i][j] == 1)
                        colour = "blue";
                    else if(pieces_colour[i][j] == 2)
                        colour = "red";
                    else if(pieces_colour[i][j] == 3)
                        colour = "green";
                    else if(pieces_colour[i][j] == 4)
                        colour = "yellow";
                    else
                        colour = "";

                    String piece_type;

                    if(pieces_type[i][j] == 1)
                        piece_type = "pawn";
                    else if(pieces_type[i][j] == 2)
                        piece_type = "boat";
                    else if(pieces_type[i][j] == 3)
                        piece_type = "knight";
                    else if(pieces_type[i][j] == 4)
                        piece_type = "elephant";
                    else if(pieces_type[i][j] == 5)
                        piece_type = "king";
                    else
                        piece_type = "";

                    String piece = colour + piece_type;
                    int identifier = getResources().getIdentifier(piece, "drawable", GameActivity.this.getPackageName());
                    Board[i][j].setImageResource(identifier);
                }
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
                Board[column][row].setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        if ((selected_column != -1) && (selected_row != -1) && valid_moves[column][row])
                        {
                            move(selected_column, selected_row, column, row);
                            moved = true;
                        }

                        clear_selections();

                        if ((!moved) && select_piece(column, row))
                        {
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

    public boolean select_piece(int column, int row) {

        if(pieces_colour[column][row] != 0)
        {
            Board[column][row].setBackgroundColor(getResources().getColor(R.color.light_blue));
            return true;
        }

        return false;
    }

    public void clear_selections() {

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                Board[i][j].setBackgroundColor(getResources().getColor(R.color.transparent));
                valid_moves[i][j] = false;
            }

        selected_row = -1;
        selected_column = -1;
    }

    public void show_valid_moves(int column, int row) {

        if(pieces_type[column][row] == 1)
            pawn_valid_moves(column, row);
        else if(pieces_type[column][row] == 2)
            boat_valid_moves(column, row);
        else if(pieces_type[column][row] == 3)
            knight_valid_moves(column, row);
        else if(pieces_type[column][row] == 4)
            elephant_valid_moves(column, row);
        else if(pieces_type[column][row] == 5)
            king_valid_moves(column, row);

    }

    public void pawn_valid_moves(int column, int row) {

        if(pieces_colour[column][row] == 1 && (row <= 6))
        {

            if(pieces_colour[column][row + 1] == 0)
            {
                Board[column][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column][row + 1] = true;
            }

            if(column <= 6)
            {
                if((pieces_colour[column + 1][row + 1] == 2) || (pieces_colour[column + 1][row + 1] == 3) || (pieces_colour[column + 1][row + 1] == 4))
                {
                    Board[column + 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column + 1][row + 1] = true;
                }
            }

            if(column >= 1)
            {
                if((pieces_colour[column - 1][row + 1] == 2) || (pieces_colour[column - 1][row + 1] == 3) || (pieces_colour[column - 1][row + 1] == 4))
                {
                    Board[column - 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column - 1][row + 1] = true;
                }
            }
        }

        if((pieces_colour[column][row] == 2) && (column <= 6))
        {
            if(pieces_colour[column + 1][row] == 0)
            {
                Board[column + 1][row].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 1][row] = true;
            }

            if(row <= 6)
            {
                if((pieces_colour[column + 1][row + 1] == 1) || (pieces_colour[column + 1][row + 1] == 3) || (pieces_colour[column + 1][row + 1] == 4))
                {
                    Board[column + 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column + 1][row + 1] = true;
                }
            }

            if(row >= 1)
            {
                if((pieces_colour[column + 1][row - 1] == 1) || (pieces_colour[column + 1][row - 1] == 3) || (pieces_colour[column + 1][row - 1] == 4))
                {
                    Board[column + 1][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column + 1][row - 1] = true;
                }
            }
        }

        if((pieces_colour[column][row] == 3) && (row >= 1))
        {
            if(pieces_colour[column][row - 1] == 0)
            {
                Board[column][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column][row - 1] = true;
            }

            if(column <= 6)
            {
                if((pieces_colour[column + 1][row - 1] == 1) || (pieces_colour[column + 1][row - 1] == 2) || (pieces_colour[column + 1][row - 1] == 4))
                {
                    Board[column + 1][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column + 1][row - 1] = true;
                }
            }

            if(column >= 1)
            {
                if((pieces_colour[column - 1][row - 1] == 1) || (pieces_colour[column - 1][row - 1] == 2) || (pieces_colour[column - 1][row - 1] == 4))
                {
                    Board[column - 1][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column - 1][row - 1] = true;
                }
            }
        }

        if((pieces_colour[column][row] == 4) && (column >= 1))
        {
            if(pieces_colour[column - 1][row] == 0)
            {
                Board[column - 1][row].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 1][row] = true;
            }

            if(row <= 6)
            {
                if((pieces_colour[column - 1][row + 1] == 1) || (pieces_colour[column - 1][row + 1] == 2) || (pieces_colour[column - 1][row + 1] == 3))
                {
                    Board[column - 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column - 1][row + 1] = true;
                }
            }

            if(row >= 1)
            {
                if((pieces_colour[column - 1][row - 1] == 1) || (pieces_colour[column - 1][row - 1] == 2) || (pieces_colour[column - 1][row - 1] == 3))
                {
                    Board[column - 1][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column - 1][row - 1] = true;
                }
            }
        }
    }

    public void boat_valid_moves(int column, int row) {

        if((column <= 5) && (row <= 5))
        {
            if(pieces_colour[column][row] != pieces_colour[column + 2][row + 2])
            {
                Board[column + 2][row + 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 2][row + 2] = true;
            }
        }

        if((column >= 2) && (row >= 2))
        {
            if(pieces_colour[column][row] != pieces_colour[column - 2][row - 2])
            {
                Board[column - 2][row - 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 2][row - 2] = true;
            }
        }

        if((column >= 2) && (row <= 5))
        {
            if(pieces_colour[column][row] != pieces_colour[column - 2][row + 2])
            {
                Board[column - 2][row + 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 2][row + 2] = true;
            }
        }

        if((column <= 5) && (row >= 2))
        {
            if(pieces_colour[column][row] != pieces_colour[column + 2][row - 2])
            {
                Board[column + 2][row - 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 2][row - 2] = true;
            }
        }
    }

    public void knight_valid_moves(int column, int row) {

        if((column >= 1) && (row <= 5))
        {
            if(pieces_colour[column][row] != pieces_colour[column - 1][row + 2])
            {
                Board[column - 1][row + 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 1][row + 2] = true;
            }
        }

        if((column <= 6) && (row <= 5))
        {
            if(pieces_colour[column][row] != pieces_colour[column + 1][row + 2])
            {
                Board[column + 1][row + 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 1][row + 2] = true;
            }
        }

        if((column >= 1) && (row >= 2))
        {
            if(pieces_colour[column][row] != pieces_colour[column - 1][row - 2])
            {
                Board[column - 1][row - 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 1][row - 2] = true;
            }
        }

        if((column <= 6) && (row >= 2))
        {
            if(pieces_colour[column][row] != pieces_colour[column + 1][row - 2])
            {
                Board[column + 1][row - 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 1][row - 2] = true;
            }
        }

        if((column >= 2) && (row >= 1))
        {
            if(pieces_colour[column][row] != pieces_colour[column - 2][row - 1])
            {
                Board[column - 2][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 2][row - 1] = true;
            }
        }

        if((column <= 5) && (row >= 1))
        {
            if(pieces_colour[column][row] != pieces_colour[column + 2][row - 1])
            {
                Board[column + 2][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 2][row - 1] = true;
            }
        }

        if((column >= 2) && (row <= 6))
        {
            if(pieces_colour[column][row] != pieces_colour[column - 2][row + 1])
            {
                Board[column - 2][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 2][row + 1] = true;
            }
        }

        if((column <= 5) && (row <= 6))
        {
            if(pieces_colour[column][row] != pieces_colour[column + 2][row + 1])
            {
                Board[column + 2][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 2][row + 1] = true;
            }
        }
    }

    public void elephant_valid_moves(int column, int row) {

        for(int i = 1; i <= (7 - column); i++)
        {
            if(pieces_colour[column][row] == pieces_colour[column + i][row])
                break;

            Board[column + i][row].setBackgroundColor(getResources().getColor(R.color.light_blue));
            valid_moves[column + i][row] = true;

            if(pieces_colour[column + i][row] != 0)
                break;
        }

        for(int i = 1; i <= column; i++)
        {
            if(pieces_colour[column][row] == pieces_colour[column - i][row])
                break;

            Board[column - i][row].setBackgroundColor(getResources().getColor(R.color.light_blue));
            valid_moves[column - i][row] = true;

            if(pieces_colour[column - i][row] != 0)
                break;
        }

        for(int i = 1; i <= (7 - row); i++)
        {
            if(pieces_colour[column][row] == pieces_colour[column][row + i])
                break;

            Board[column][row + i].setBackgroundColor(getResources().getColor(R.color.light_blue));
            valid_moves[column][row + i] = true;

            if(pieces_colour[column][row + i] != 0)
                break;
        }

        for(int i = 1; i <= row; i++)
        {
            if(pieces_colour[column][row] == pieces_colour[column][row - i])
                break;

            Board[column][row - i].setBackgroundColor(getResources().getColor(R.color.light_blue));
            valid_moves[column][row - i] = true;

            if(pieces_colour[column][row - i] != 0)
                break;
        }
    }

    public void king_valid_moves(int column, int row) {

        if(column >= 1)
        {
            if(pieces_colour[column][row] != pieces_colour[column - 1][row])
            {
                Board[column - 1][row].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 1][row] = true;
            }
        }

        if(column <= 6)
        {
            if(pieces_colour[column][row] != pieces_colour[column + 1][row])
            {
                Board[column + 1][row].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 1][row] = true;
            }
        }

        if(row >= 1)
        {
            if(pieces_colour[column][row] != pieces_colour[column][row - 1])
            {
                Board[column][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column][row - 1] = true;
            }
        }

        if(row <= 6)
        {
            if(pieces_colour[column][row] != pieces_colour[column][row + 1])
            {
                Board[column][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column][row + 1] = true;
            }
        }

        if((column >= 1) && (row >= 1))
        {
            if(pieces_colour[column][row] != pieces_colour[column - 1][row - 1])
            {
                Board[column - 1][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 1][row - 1] = true;
            }
        }

        if((column >= 1) && (row <= 6))
        {
            if(pieces_colour[column][row] != pieces_colour[column - 1][row + 1])
            {
                Board[column - 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 1][row + 1] = true;
            }
        }

        if((column <= 6) && (row <= 6))
        {
            if(pieces_colour[column][row] != pieces_colour[column + 1][row + 1])
            {
                Board[column + 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 1][row + 1] = true;
            }
        }

        if((column <= 6) && (row >= 1))
        {
            if(pieces_colour[column][row] != pieces_colour[column + 1][row - 1])
            {
                Board[column + 1][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 1][row - 1] = true;
            }
        }
    }

    public void move(int source_column, int source_row, int destination_column, int destination_row) {

        String source_tag;

        if(pieces_type[source_column][source_row] == 1)
            source_tag = "pawn";
        else if(pieces_type[source_column][source_row] == 2)
            source_tag = "boat";
        else if(pieces_type[source_column][source_row] == 3)
            source_tag = "knight";
        else if(pieces_type[source_column][source_row] == 4)
            source_tag = "elephant";
        else if(pieces_type[source_column][source_row] == 5)
            source_tag = "king";
        else
            source_tag = "";

        String piece_colour;

        if(pieces_colour[source_column][source_row] == 1)
            piece_colour = "blue";
        else if(pieces_colour[source_column][source_row] == 2)
            piece_colour = "red";
        else if(pieces_colour[source_column][source_row] == 3)
            piece_colour = "green";
        else if(pieces_colour[source_column][source_row] == 4)
            piece_colour = "yellow";
        else
            piece_colour = "empty";

        String image = piece_colour + source_tag;

        int identifier = getResources().getIdentifier(image, "drawable", GameActivity.this.getPackageName());

        pieces_type[destination_column][destination_row] = pieces_type[source_column][source_row];
        pieces_type[source_column][source_row] = 0;

        pieces_colour[destination_column][destination_row] = pieces_colour[source_column][source_row];
        pieces_colour[source_column][source_row] = 0;

        Board[destination_column][destination_row].setImageResource(identifier);
        Board[source_column][source_row].setImageResource(0);
    }


}
