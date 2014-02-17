package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChatuService;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.Menu;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.swing.text.html.ImageView;
import java.awt.*;


/**
 * Created by Haider on 12/02/14.
 */
public class GameActivity extends Activity {

    private final android.widget.ImageView[][] Board = new android.widget.ImageView[8][8];
    private boolean[][] valid_moves = new boolean[8][8];
    private int[][] pieces = new int[8][8];
    private int counter = 0;
    private boolean selected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.in_game);

        /* Following code done by Kadir Sekha */

        set_pieces();

        Board[0][0] = (android.widget.ImageView) findViewById(R.id.A1);
        Board[1][0] = (android.widget.ImageView) findViewById(R.id.B1);
        Board[2][0] = (android.widget.ImageView) findViewById(R.id.C1);
        Board[3][0] = (android.widget.ImageView) findViewById(R.id.D1);
        Board[4][0] = (android.widget.ImageView) findViewById(R.id.E1);
        Board[5][0] = (android.widget.ImageView) findViewById(R.id.F1);
        Board[6][0] = (android.widget.ImageView) findViewById(R.id.G1);
        Board[7][0] = (android.widget.ImageView) findViewById(R.id.H1);
        Board[0][1] = (android.widget.ImageView) findViewById(R.id.A2);
        Board[1][1] = (android.widget.ImageView) findViewById(R.id.B2);
        Board[2][1] = (android.widget.ImageView) findViewById(R.id.C2);
        Board[3][1] = (android.widget.ImageView) findViewById(R.id.D2);
        Board[4][1] = (android.widget.ImageView) findViewById(R.id.E2);
        Board[5][1] = (android.widget.ImageView) findViewById(R.id.F2);
        Board[6][1] = (android.widget.ImageView) findViewById(R.id.G2);
        Board[7][1] = (android.widget.ImageView) findViewById(R.id.H2);
        Board[0][2] = (android.widget.ImageView) findViewById(R.id.A3);
        Board[1][2] = (android.widget.ImageView) findViewById(R.id.B3);
        Board[2][2] = (android.widget.ImageView) findViewById(R.id.C3);
        Board[3][2] = (android.widget.ImageView) findViewById(R.id.D3);
        Board[4][2] = (android.widget.ImageView) findViewById(R.id.E3);
        Board[5][2] = (android.widget.ImageView) findViewById(R.id.F3);
        Board[6][2] = (android.widget.ImageView) findViewById(R.id.G3);
        Board[7][2] = (android.widget.ImageView) findViewById(R.id.H3);
        Board[0][3] = (android.widget.ImageView) findViewById(R.id.A4);
        Board[1][3] = (android.widget.ImageView) findViewById(R.id.B4);
        Board[2][3] = (android.widget.ImageView) findViewById(R.id.C4);
        Board[3][3] = (android.widget.ImageView) findViewById(R.id.D4);
        Board[4][3] = (android.widget.ImageView) findViewById(R.id.E4);
        Board[5][3] = (android.widget.ImageView) findViewById(R.id.F4);
        Board[6][3] = (android.widget.ImageView) findViewById(R.id.G4);
        Board[7][3] = (android.widget.ImageView) findViewById(R.id.H4);
        Board[0][4] = (android.widget.ImageView) findViewById(R.id.A5);
        Board[1][4] = (android.widget.ImageView) findViewById(R.id.B5);
        Board[2][4] = (android.widget.ImageView) findViewById(R.id.C5);
        Board[3][4] = (android.widget.ImageView) findViewById(R.id.D5);
        Board[4][4] = (android.widget.ImageView) findViewById(R.id.E5);
        Board[5][4] = (android.widget.ImageView) findViewById(R.id.F5);
        Board[6][4] = (android.widget.ImageView) findViewById(R.id.G5);
        Board[7][4] = (android.widget.ImageView) findViewById(R.id.H5);
        Board[0][5] = (android.widget.ImageView) findViewById(R.id.A6);
        Board[1][5] = (android.widget.ImageView) findViewById(R.id.B6);
        Board[2][5] = (android.widget.ImageView) findViewById(R.id.C6);
        Board[3][5] = (android.widget.ImageView) findViewById(R.id.D6);
        Board[4][5] = (android.widget.ImageView) findViewById(R.id.E6);
        Board[5][5] = (android.widget.ImageView) findViewById(R.id.F6);
        Board[6][5] = (android.widget.ImageView) findViewById(R.id.G6);
        Board[7][5] = (android.widget.ImageView) findViewById(R.id.H6);
        Board[0][6] = (android.widget.ImageView) findViewById(R.id.A7);
        Board[1][6] = (android.widget.ImageView) findViewById(R.id.B7);
        Board[2][6] = (android.widget.ImageView) findViewById(R.id.C7);
        Board[3][6] = (android.widget.ImageView) findViewById(R.id.D7);
        Board[4][6] = (android.widget.ImageView) findViewById(R.id.E7);
        Board[5][6] = (android.widget.ImageView) findViewById(R.id.F7);
        Board[6][6] = (android.widget.ImageView) findViewById(R.id.G7);
        Board[7][6] = (android.widget.ImageView) findViewById(R.id.H7);
        Board[0][7] = (android.widget.ImageView) findViewById(R.id.A8);
        Board[1][7] = (android.widget.ImageView) findViewById(R.id.B8);
        Board[2][7] = (android.widget.ImageView) findViewById(R.id.C8);
        Board[3][7] = (android.widget.ImageView) findViewById(R.id.D8);
        Board[4][7] = (android.widget.ImageView) findViewById(R.id.E8);
        Board[5][7] = (android.widget.ImageView) findViewById(R.id.F8);
        Board[6][7] = (android.widget.ImageView) findViewById(R.id.G8);
        Board[7][7] = (android.widget.ImageView) findViewById(R.id.H8);

        final TextView display = (TextView) findViewById(R.id.testdisplay);

        int i;
        int j;

        for(i = 0; i < 8; i++)
        {
            final int row = i;
            for(j = 0; j < 8; j++)
            {
                final int column = j;
                Board[row][column].setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        clear_selections();
                        selected = select_piece(row, column);

                        if(selected)
                            show_valid_moves(row, column);
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
            }
    }

    public void show_valid_moves(int column, int row) {

        if(Board[column][row].getTag().equals("pawn"))
        {
            if(pieces[column][row] == 1 && (row <= 6))
            {

                if(pieces[column][row + 1] == 0)
                    Board[column][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));

                if(column <= 6)
                {
                    if((pieces[column + 1][row + 1] == 2) || (pieces[column + 1][row + 1] == 3) || (pieces[column + 1][row + 1] == 4))
                        Board[column + 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                }

                if(column >= 1)
                {
                    if((pieces[column - 1][row + 1] == 2) || (pieces[column - 1][row + 1] == 3) || (pieces[column - 1][row + 1] == 4))
                        Board[column - 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                }
            }

            if((pieces[column][row] == 2) && (column <= 6))
            {
                if(pieces[column + 1][row] == 0)
                    Board[column + 1][row].setBackgroundColor(getResources().getColor(R.color.light_blue));

                if(row <= 6)
                {
                    if((pieces[column + 1][row + 1] == 1) || (pieces[column + 1][row + 1] == 3) || (pieces[column + 1][row + 1] == 4))
                        Board[column + 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                }

                if(row >= 1)
                {
                    if((pieces[column + 1][row - 1] == 1) || (pieces[column + 1][row - 1] == 3) || (pieces[column + 1][row - 1] == 4))
                        Board[column + 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                }
            }

            if((pieces[column][row] == 3) && (row >= 1))
            {
                if(pieces[column][row - 1] == 0)
                    Board[column][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));

                if(column <= 6)
                {
                    if((pieces[column + 1][row - 1] == 1) || (pieces[column + 1][row - 1] == 2) || (pieces[column + 1][row - 1] == 4))
                        Board[column + 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                }

                if(column >= 1)
                {
                    if((pieces[column - 1][row - 1] == 1) || (pieces[column - 1][row - 1] == 2) || (pieces[column - 1][row - 1] == 4))
                        Board[column + 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                }
            }

            if((pieces[column][row] == 4) && (column <= 6))
            {
                if(pieces[column - 1][row] == 0)
                    Board[column - 1][row].setBackgroundColor(getResources().getColor(R.color.light_blue));

                if(row <= 6)
                {
                    if((pieces[column - 1][row + 1] == 1) || (pieces[column - 1][row + 1] == 2) || (pieces[column + 1][row + 1] == 3))
                        Board[column + 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                }

                if(row >= 1)
                {
                    if((pieces[column - 1][row - 1] == 1) || (pieces[column - 1][row - 1] == 2) || (pieces[column - 1][row - 1] == 3))
                        Board[column + 1][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
                }
            }
        }

        if(Board[column][row].getTag().equals("boat"))
        {
            if((column <= 5) && (row <= 5))
            {
                if(pieces[column][row] != pieces[column + 2][row + 2])
                    Board[column + 2][row + 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
            }

            if((column >= 2) && (row >= 2))
            {
                if(pieces[column][row] != pieces[column - 2][row - 2])
                    Board[column - 2][row - 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
            }

            if((column >= 2) && (row <= 5))
            {
                if(pieces[column][row] != pieces[column - 2][row + 2])
                    Board[column - 2][row + 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
            }

            if((column <= 5) && (row >= 2))
            {
                if(pieces[column][row] != pieces[column + 2][row - 2])
                    Board[column + 2][row - 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
            }
        }

        if(Board[column][row].getTag().equals("knight"))
        {
            if((column >= 1) && (row <= 5))
            {
                if(pieces[column][row] != pieces[column - 1][row + 2])
                    Board[column - 1][row + 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
            }

            if((column <= 6) && (row <= 5))
            {
                if(pieces[column][row] != pieces[column + 1][row + 2])
                    Board[column + 1][row + 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
            }

            if((column >= 1) && (row >= 2))
            {
                if(pieces[column][row] != pieces[column - 1][row - 2])
                    Board[column - 1][row - 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
            }

            if((column <= 6) && (row >= 2))
            {
                if(pieces[column][row] != pieces[column + 1][row - 2])
                    Board[column + 1][row - 2].setBackgroundColor(getResources().getColor(R.color.light_blue));
            }

            if((column >= 2) && (row >= 1))
            {
                if(pieces[column][row] != pieces[column - 2][row - 1])
                    Board[column - 2][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
            }

            if((column <= 5) && (row >= 1))
            {
                if(pieces[column][row] != pieces[column + 2][row - 1])
                    Board[column + 2][row - 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
            }

            if((column >= 2) && (row <= 6))
            {
                if(pieces[column][row] != pieces[column - 2][row + 1])
                    Board[column - 2][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
            }

            if((column <= 5) && (row <= 6))
            {
                if(pieces[column][row] != pieces[column + 2][row + 1])
                    Board[column + 2][row + 1].setBackgroundColor(getResources().getColor(R.color.light_blue));
            }
        }

        //NEED TO DO THE SAME FOR ELEPHANT AND KING

    }

}
