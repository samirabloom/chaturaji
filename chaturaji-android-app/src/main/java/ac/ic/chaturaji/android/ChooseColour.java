package ac.ic.chaturaji.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * @author ks513
 */
public class ChooseColour extends Activity {

    Button blue_button;
    Button red_button;
    Button green_button;
    Button yellow_button;
    String colour;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.choose_colour);

        final Intent game = new Intent(ChooseColour.this, GameActivity.class);

        blue_button = (Button) findViewById(R.id.blue_button);
        red_button = (Button) findViewById(R.id.red_button);
        green_button = (Button) findViewById(R.id.green_button);
        yellow_button = (Button) findViewById(R.id.yellow_button);

        blue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colour = "in_game_blue";
                game.putExtra("colour", colour);
                startActivity(game);
            }
        });

        red_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colour = "in_game_red";
                game.putExtra("colour", colour);
                startActivity(game);
            }
        });

        green_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colour = "in_game_green";
                game.putExtra("colour", colour);
                startActivity(game);
            }
        });

        yellow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colour = "in_game_yellow";
                game.putExtra("colour", colour);
                startActivity(game);
            }
        });
    }
}
