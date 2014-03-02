package ac.ic.chaturaji.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

/**
 * Created by Haider on 18/02/14.
 */
public class SettingsActivity extends Activity {

    private CheckBox soundcbx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.settings_main);

        SharedPreferences settings = getSharedPreferences("main", 0);
        boolean soundOn = settings.getBoolean("sound", true);

        soundcbx = (CheckBox) findViewById(R.id.soundbox);

        if(soundOn)
            soundcbx.setChecked(true);
        else
            soundcbx.setChecked(false);

        soundcbx.setOnClickListener(soundSettings);

    }

    private View.OnClickListener soundSettings = new View.OnClickListener(){

        @Override
        public void onClick(View theView) {

            if(((CheckBox)theView).isChecked()){

                SharedPreferences settings = getSharedPreferences("main", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("sound", true);
                editor.commit();

            }

            else {

                SharedPreferences settings = getSharedPreferences("main", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("sound", false);
                editor.commit();

            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
