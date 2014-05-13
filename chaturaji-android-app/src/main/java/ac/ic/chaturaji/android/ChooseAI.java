package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChaturajiService;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import java.util.Arrays;

/**
 * Created by Haider on 13/05/14.
 */
public class ChooseAI extends Activity {

    private static final String TAG = "ChooseAI";
    Button start_game_button;
    SeekBar ai_diff;
    TextView diffText;

    String numberOfAI = "0";
    String aiDiff = "2";

    private View.OnClickListener singleButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View theView) {

            Intent getSingleGame = new Intent(ChooseAI.this, GameActivity.class);

            try {

                String[] state = new CreateGame().execute("3").get();

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

                getSingleGame.putExtra("colour", colour);

                if (Arrays.asList(state).contains("Error")) {
                    Toast.makeText(getApplicationContext(), "Sorry, there was a problem connecting with server..", Toast.LENGTH_LONG).show();
                } else if (Arrays.asList(state).contains("Invalid")) {
                    Toast.makeText(getApplicationContext(), "Sorry, there was a problem logging in.", Toast.LENGTH_LONG).show();
                } else if (Arrays.asList(state).contains("401")) {
                    Toast.makeText(getApplicationContext(), "Unauthorized, perhaps your session has run out.", Toast.LENGTH_LONG).show();
                    Intent logOut = new Intent(ChooseAI.this, LoginActivity.class);
                    ChaturajiService chaturajiService = ChaturajiService.getInstance();
                    chaturajiService.logout();
                    chaturajiService.clearCookieCred();
                    startActivity(logOut);
                } else {
                    startActivity(getSingleGame);
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

        }
    };

    private class CreateGame extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... AIs) {
            ChaturajiService chaturajiService = ChaturajiService.getInstance();

            String[] state = chaturajiService.createGame(AIs[0], aiDiff);

            Log.d(TAG, "CreateGame status " + Arrays.asList(state));

            return state;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.choose_diff);


        start_game_button = (Button) findViewById(R.id.start_game_button_c);
        ai_diff = (SeekBar) findViewById(R.id.ai_diff_sb_c);
        diffText = (TextView) findViewById(R.id.diff_txt_c);

        start_game_button.setOnClickListener(singleButtonListener);
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
}
