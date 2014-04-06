package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChatuService;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;

public class MainMenu extends Activity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */

    Button single_player_button;
    Button multi_player_button;
    Button settings_button;
    Button logout_button;
    String email;
    String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main_menu);

        SharedPreferences settings = getSharedPreferences("main", 0);
        email = settings.getString("email", "unknown");
        password = settings.getString("password", "unknown");


        single_player_button = (Button) findViewById(R.id.single_player_button);
        multi_player_button = (Button) findViewById(R.id.multi_player_button);
        settings_button = (Button) findViewById(R.id.settings_button);
        logout_button = (Button) findViewById(R.id.log_out_button);

        single_player_button.setOnClickListener(singleButtonListener);
        multi_player_button.setOnClickListener(multiButttonListener);
        settings_button.setOnClickListener(settingsButttonListener);
        logout_button.setOnClickListener(logoutButttonListener);
    }

    private View.OnClickListener singleButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View theView) {

            Intent getSingleGame = new Intent(MainMenu.this, GameActivity.class);

            try {

                String[] state = new PostGame().execute("3").get();
                System.out.println(Arrays.asList(state));

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
                    Intent logOut = new Intent(MainMenu.this, LoginActivity.class);
                    ChatuService chatuService = ChatuService.getInstance();
                    chatuService.logout();
                    chatuService.clearCookieCred();
                    startActivity(logOut);
                }else {
                    startActivity(getSingleGame);
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

        }
    };

    private View.OnClickListener multiButttonListener = new View.OnClickListener() {

        @Override
        public void onClick(View theView) {

            Intent getGameRooms = new Intent(MainMenu.this, GameRoomActivity.class);
            startActivity(getGameRooms);
        }
    };

    private View.OnClickListener settingsButttonListener = new View.OnClickListener() {

        @Override
        public void onClick(View theView) {

            Intent getSettings = new Intent(MainMenu.this, SettingsActivity.class);
            startActivity(getSettings);
        }
    };

    private View.OnClickListener logoutButttonListener = new View.OnClickListener() {

        @Override
        public void onClick(View theView) {

            Intent logOut = new Intent(MainMenu.this, LoginActivity.class);
            logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            ChatuService chatuService = ChatuService.getInstance();

            chatuService.logout();
            chatuService.clearCookieCred();

            startActivity(logOut);
        }
    };


    private class PostGame extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... AIs) {
            ChatuService chatuService = ChatuService.getInstance();

            chatuService.setEmailPassword(email, password);

            String[] state = chatuService.createGame(AIs[0]);

            System.out.println(Arrays.asList(state));

            return state;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}

