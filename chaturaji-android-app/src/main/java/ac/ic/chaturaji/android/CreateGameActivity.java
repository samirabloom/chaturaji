package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChaturajiService;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import java.util.Arrays;

/**
 * @author Haider
 */
public class CreateGameActivity extends Activity {

    private static final String TAG = "CreateGameActivity";
    Button start_game_button;
    RadioGroup radioAI;
    RadioButton radioAI0;
    RadioButton radioAI1;
    RadioButton radioAI2;
    RadioButton radioAI3;
    SeekBar ai_diff;
    TextView diffText;

    String numberOfAI = "0";
    String aiDiff = "2";
    public View.OnClickListener startButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View theView) {

            Intent startGame = new Intent(CreateGameActivity.this, GameActivity.class);

            CreateGame createGame = new CreateGame();

            try {

                createGame.execute(numberOfAI);
                String[] state = createGame.get();
                Log.d(TAG, "CreateGame status " + Arrays.asList(state));

                String colour = "in_game_yellow";

                switch (state[1]) {
                    case "BLUE":
                        colour = "in_game_blue";
                        break;
                    case "RED":
                        colour = "in_game_red";
                        break;
                    case "GREEN":
                        colour = "in_game_green";
                        break;
                }

                startGame.putExtra("colour", colour);

                switch (state[0]) {
                    case "Error":
                        Toast.makeText(getApplicationContext(), "Sorry, there was a problem connecting with server..", Toast.LENGTH_LONG).show();
                        break;
                    case "401":
                        Toast.makeText(getApplicationContext(), "Unauthorized, perhaps your session has run out.", Toast.LENGTH_LONG).show();
                        Intent logOut = new Intent(CreateGameActivity.this, LoginActivity.class);
                        logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        ChaturajiService chaturajiService = ChaturajiService.getInstance();
                        chaturajiService.logout();
                        chaturajiService.clearCookieCred();
                        startActivity(logOut);
                        break;
                    default:
                        startGame.putExtra("REPLAY", false);
                        startActivity(startGame);
                        break;
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    };
    public View.OnClickListener radioAI0Listener = new View.OnClickListener() {

        @Override
        public void onClick(View theView) {

            numberOfAI = String.valueOf(radioAI0.getText());
        }
    };
    public View.OnClickListener radioAI1Listener = new View.OnClickListener() {

        @Override
        public void onClick(View theView) {

            numberOfAI = String.valueOf(radioAI1.getText());
        }
    };
    public View.OnClickListener radioAI2Listener = new View.OnClickListener() {

        @Override
        public void onClick(View theView) {

            numberOfAI = String.valueOf(radioAI2.getText());
        }
    };
    public View.OnClickListener radioAI3Listener = new View.OnClickListener() {

        @Override
        public void onClick(View theView) {

            numberOfAI = String.valueOf(radioAI3.getText());
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.create_game);


        start_game_button = (Button) findViewById(R.id.start_game_button);
        radioAI = (RadioGroup) findViewById(R.id.radioAI);
        radioAI0 = (RadioButton) findViewById(R.id.radioAI0);
        radioAI1 = (RadioButton) findViewById(R.id.radioAI1);
        radioAI2 = (RadioButton) findViewById(R.id.radioAI2);
        radioAI3 = (RadioButton) findViewById(R.id.radioAI3);
        ai_diff = (SeekBar) findViewById(R.id.ai_diff_sb);
        diffText = (TextView) findViewById(R.id.diff_txt);

        start_game_button.setOnClickListener(startButtonListener);
        radioAI0.setOnClickListener(radioAI0Listener);
        radioAI1.setOnClickListener(radioAI1Listener);
        radioAI2.setOnClickListener(radioAI2Listener);
        radioAI3.setOnClickListener(radioAI3Listener);
        ai_diff.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int progress = 2;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                        progress = progresValue + 2;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        aiDiff = Integer.toString(progress);
                        diffText.setText(aiDiff);
                    }
                });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class CreateGame extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... AIs) {
            return ChaturajiService.getInstance().createGame(AIs[0], aiDiff);
        }

    }
}
