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


public class GameActivity extends Activity {

    private final android.widget.ImageView[][] Board = new android.widget.ImageView[8][8];
    private int[][] pieces = new int[8][8];
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

        setContentView(R.layout.in_game_blue);

        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Intent intent =getIntent();
        numberOfAIs = intent.getStringExtra("numberOfAI");
        System.out.println(numberOfAIs);
        PostGame postgame = new PostGame();
        postgame.execute(numberOfAIs);

        /* Following code done by Kadir Sekha */

        set_pieces();
        draw_pieces();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //This method needs sorting out, need to find a way so the game doesn't restart
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.in_game_blue);
        draw_pieces();
    }

    public void set_pieces() {

        //0 if empty
        //1 if blue
        //2 if red
        //3 if green
        //4 if yellow

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                pieces[i][j] = 0;
            }

        for(int i = 0; i <= 3; i++)
            for(int j = 0; j <= 1; j++)
            {
                pieces[i][j] = 1;
            }

        for(int i = 0; i <= 1; i++)
            for(int j = 4; j <= 7; j++)
            {
                pieces[i][j] = 2;
            }

        for(int i = 4; i <= 7; i++)
            for(int j = 6; j <= 7; j++)
            {
                pieces[i][j] = 3;
            }

        for(int i = 6; i <= 7; i++)
            for(int j = 0; j <= 3; j++)
            {
                pieces[i][j] = 4;
            }

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
                valid_moves[i][j] = false;

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                char column_letter = (char)('A' + i);
                char row_number = (char)('1' + j);
                String square = "" + column_letter + row_number;
                int identifier = getResources().getIdentifier(square, "id", GameActivity.this.getPackageName());
                Board[i][j] = (android.widget.ImageView) findViewById(identifier);
            }
    }

    public void draw_pieces() {

        for(int i = 0; i < 8; i++)
            for(int j = 0; j < 8; j++)
            {
                if(pieces[i][j] != 0)
                {
                    String colour;
                    if(pieces[i][j] == 1)
                        colour = "blue";
                    else if(pieces[i][j] == 2)
                        colour = "red";
                    else if(pieces[i][j] == 3)
                        colour = "green";
                    else if(pieces[i][j] == 4)
                        colour = "yellow";
                    else
                        colour = "";

                    String piece_type;

                    if(Board[i][j].getTag().equals("pawn"))
                        piece_type = "pawn";
                    else if(Board[i][j].getTag().equals("boat"))
                        piece_type = "boat";
                    else if(Board[i][j].getTag().equals("knight"))
                        piece_type = "knight";
                    else if(Board[i][j].getTag().equals("elephant"))
                        piece_type = "elephant";
                    else if(Board[i][j].getTag().equals("king"))
                        piece_type = "king";
                    else
                        piece_type = "";

                    String piece = colour + piece_type;
                    int identifier = getResources().getIdentifier(piece, "drawable", GameActivity.this.getPackageName());
                    Board[i][j].setImageResource(identifier);
                }
            }
    }

    public boolean select_piece(int column, int row) {

        if(pieces[column][row] != 0)
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

    //Need to add elephant to this method
    public void show_valid_moves(int column, int row) {

        if(Board[column][row].getTag().equals("pawn"))
            pawn_valid_moves(column, row);
        else if(Board[column][row].getTag().equals("boat"))
            boat_valid_moves(column, row);
        else if(Board[column][row].getTag().equals("knight"))
            knight_valid_moves(column, row);
        else if(Board[column][row].getTag().equals("king"))
            king_valid_moves(column, row);

    }

    // Add elephant valid moves
    public void pawn_valid_moves(int column, int row) {

        if(pieces[column][row] == 1 && (row <= 6))
        {

            if(pieces[column][row + 1] == 0)
            {
                Board[column][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column][row + 1] = true;
            }

            if(column <= 6)
            {
                if((pieces[column + 1][row + 1] == 2) || (pieces[column + 1][row + 1] == 3) || (pieces[column + 1][row + 1] == 4))
                {
                    Board[column + 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column + 1][row + 1] = true;
                }
            }

            if(column >= 1)
            {
                if((pieces[column - 1][row + 1] == 2) || (pieces[column - 1][row + 1] == 3) || (pieces[column - 1][row + 1] == 4))
                {
                    Board[column - 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column - 1][row + 1] = true;
                }
            }
        }

        if((pieces[column][row] == 2) && (column <= 6))
        {
            if(pieces[column + 1][row] == 0)
            {
                Board[column + 1][row].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 1][row] = true;
            }

            if(row <= 6)
            {
                if((pieces[column + 1][row + 1] == 1) || (pieces[column + 1][row + 1] == 3) || (pieces[column + 1][row + 1] == 4))
                {
                    Board[column + 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column + 1][row + 1] = true;
                }
            }

            if(row >= 1)
            {
                if((pieces[column + 1][row - 1] == 1) || (pieces[column + 1][row - 1] == 3) || (pieces[column + 1][row - 1] == 4))
                {
                    Board[column + 1][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column + 1][row - 1] = true;
                }
            }
        }

        if((pieces[column][row] == 3) && (row >= 1))
        {
            if(pieces[column][row - 1] == 0)
            {
                Board[column][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column][row - 1] = true;
            }

            if(column <= 6)
            {
                if((pieces[column + 1][row - 1] == 1) || (pieces[column + 1][row - 1] == 2) || (pieces[column + 1][row - 1] == 4))
                {
                    Board[column + 1][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column + 1][row - 1] = true;
                }
            }

            if(column >= 1)
            {
                if((pieces[column - 1][row - 1] == 1) || (pieces[column - 1][row - 1] == 2) || (pieces[column - 1][row - 1] == 4))
                {
                    Board[column - 1][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column - 1][row - 1] = true;
                }
            }
        }

        if((pieces[column][row] == 4) && (column >= 1))
        {
            if(pieces[column - 1][row] == 0)
            {
                Board[column - 1][row].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 1][row] = true;
            }

            if(row <= 6)
            {
                if((pieces[column - 1][row + 1] == 1) || (pieces[column - 1][row + 1] == 2) || (pieces[column - 1][row + 1] == 3))
                {
                    Board[column - 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                    valid_moves[column - 1][row + 1] = true;
                }
            }

            if(row >= 1)
            {
                if((pieces[column - 1][row - 1] == 1) || (pieces[column - 1][row - 1] == 2) || (pieces[column - 1][row - 1] == 3))
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
            if(pieces[column][row] != pieces[column + 2][row + 2])
            {
                Board[column + 2][row + 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 2][row + 2] = true;
            }
        }

        if((column >= 2) && (row >= 2))
        {
            if(pieces[column][row] != pieces[column - 2][row - 2])
            {
                Board[column - 2][row - 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 2][row - 2] = true;
            }
        }

        if((column >= 2) && (row <= 5))
        {
            if(pieces[column][row] != pieces[column - 2][row + 2])
            {
                Board[column - 2][row + 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 2][row + 2] = true;
            }
        }

        if((column <= 5) && (row >= 2))
        {
            if(pieces[column][row] != pieces[column + 2][row - 2])
            {
                Board[column + 2][row - 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 2][row - 2] = true;
            }
        }
    }

    public void knight_valid_moves(int column, int row) {

        if((column >= 1) && (row <= 5))
        {
            if(pieces[column][row] != pieces[column - 1][row + 2])
            {
                Board[column - 1][row + 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 1][row + 2] = true;
            }
        }

        if((column <= 6) && (row <= 5))
        {
            if(pieces[column][row] != pieces[column + 1][row + 2])
            {
                Board[column + 1][row + 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 1][row + 2] = true;
            }
        }

        if((column >= 1) && (row >= 2))
        {
            if(pieces[column][row] != pieces[column - 1][row - 2])
            {
                Board[column - 1][row - 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 1][row - 2] = true;
            }
        }

        if((column <= 6) && (row >= 2))
        {
            if(pieces[column][row] != pieces[column + 1][row - 2])
            {
                Board[column + 1][row - 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 1][row - 2] = true;
            }
        }

        if((column >= 2) && (row >= 1))
        {
            if(pieces[column][row] != pieces[column - 2][row - 1])
            {
                Board[column - 2][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 2][row - 1] = true;
            }
        }

        if((column <= 5) && (row >= 1))
        {
            if(pieces[column][row] != pieces[column + 2][row - 1])
            {
                Board[column + 2][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 2][row - 1] = true;
            }
        }

        if((column >= 2) && (row <= 6))
        {
            if(pieces[column][row] != pieces[column - 2][row + 1])
            {
                Board[column - 2][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 2][row + 1] = true;
            }
        }

        if((column <= 5) && (row <= 6))
        {
            if(pieces[column][row] != pieces[column + 2][row + 1])
            {
                Board[column + 2][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 2][row + 1] = true;
            }
        }
    }

    public void king_valid_moves(int column, int row) {

        if(column >= 1)
        {
            if(pieces[column][row] != pieces[column - 1][row])
            {
                Board[column - 1][row].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 1][row] = true;
            }
        }

        if(column <= 6)
        {
            if(pieces[column][row] != pieces[column + 1][row])
            {
                Board[column + 1][row].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 1][row] = true;
            }
        }

        if(row >= 1)
        {
            if(pieces[column][row] != pieces[column][row - 1])
            {
                Board[column][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column][row - 1] = true;
            }
        }

        if(row <= 6)
        {
            if(pieces[column][row] != pieces[column][row + 1])
            {
                Board[column][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column][row + 1] = true;
            }
        }

        if((column >= 1) && (row >= 1))
        {
            if(pieces[column][row] != pieces[column - 1][row - 1])
            {
                Board[column - 1][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 1][row - 1] = true;
            }
        }

        if((column >= 1) && (row <= 6))
        {
            if(pieces[column][row] != pieces[column - 1][row + 1])
            {
                Board[column - 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column - 1][row + 1] = true;
            }
        }

        if((column <= 6) && (row <= 6))
        {
            if(pieces[column][row] != pieces[column + 1][row + 1])
            {
                Board[column + 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 1][row + 1] = true;
            }
        }

        if((column <= 6) && (row >= 1))
        {
            if(pieces[column][row] != pieces[column + 1][row - 1])
            {
                Board[column + 1][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                valid_moves[column + 1][row - 1] = true;
            }
        }
    }

    public void move(int source_column, int source_row, int destination_column, int destination_row) {

        String source_tag = (String) Board[source_column][source_row].getTag();
        String piece_colour;

        if(pieces[source_column][source_row] == 1)
            piece_colour = "blue";
        else if(pieces[source_column][source_row] == 2)
            piece_colour = "red";
        else if(pieces[source_column][source_row] == 3)
            piece_colour = "green";
        else if(pieces[source_column][source_row] == 4)
            piece_colour = "yellow";
        else
            piece_colour = "empty";

        String image = piece_colour + source_tag;

        int identifier = getResources().getIdentifier(image, "drawable", GameActivity.this.getPackageName());

        Board[destination_column][destination_row].setTag(source_tag);
        Board[source_column][source_row].setTag("empty");

        pieces[destination_column][destination_row] = pieces[source_column][source_row];
        pieces[source_column][source_row] = 0;

        Board[destination_column][destination_row].setImageResource(identifier);
        Board[source_column][source_row].setImageResource(0);
    }

    /* This makes the HTTP request thread safe - Haider's code */

    private class PostGame extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... AIs) {
            ChatuService chatuService = new ChatuService();
            String state = chatuService.createGame(AIs[0]);
            return state;
        }

        protected void onPostExecute(String state) {
            System.out.println(state);
            if(state.equals("Error")){
                Toast.makeText(getApplicationContext(), "Sorry, there was a problem connecting with server..", Toast.LENGTH_LONG).show();
            }
        }
    }
}
