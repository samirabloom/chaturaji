package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChatuService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainMenu extends Activity {

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */

    Button single_player_button;
    Button multi_player_button;
    Button settings_button;
    Button logout_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main_menu);

        /* Following code done by Haider Qazi */

        single_player_button = (Button) findViewById(R.id.single_player_button);
        multi_player_button = (Button) findViewById(R.id.multi_player_button);
        settings_button = (Button) findViewById(R.id.settings_button);
        logout_button = (Button) findViewById(R.id.log_out_button);

        single_player_button.setOnClickListener(singleButtonListener);
        multi_player_button.setOnClickListener(multiButttonListener);
        settings_button.setOnClickListener(settingsButttonListener);
        logout_button.setOnClickListener(logoutButttonListener);
    }

    public View.OnClickListener singleButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View theView) {

            Intent getSingleGame = new Intent(MainMenu.this, GameActivity.class);
            getSingleGame.putExtra("numberOfAI", "3");
            startActivity(getSingleGame);
        }
    };

    public View.OnClickListener multiButttonListener = new View.OnClickListener(){

        @Override
        public void onClick(View theView) {

            Intent getGameRooms = new Intent(MainMenu.this, GameRoomActivity.class);
            startActivity(getGameRooms);
        }
    };

    public View.OnClickListener settingsButttonListener = new View.OnClickListener(){

        @Override
        public void onClick(View theView) {

            Intent getSettings = new Intent(MainMenu.this, SettingsActivity.class);
            startActivity(getSettings);
        }
    };

    public View.OnClickListener logoutButttonListener = new View.OnClickListener(){

        @Override
        public void onClick(View theView) {

            Intent logOut = new Intent(MainMenu.this, LoginActivity.class);
            startActivity(logOut);
        }
    };

    /* End of Haider's code block */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}

