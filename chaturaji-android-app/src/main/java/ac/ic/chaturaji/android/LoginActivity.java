package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChatuService;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.content.Intent;

/**
 * Created by Haider on 12/02/14.
 */
public class LoginActivity extends Activity {

    Button login_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        login_button = (Button) findViewById(R.id.login_button);

        login_button.setOnClickListener(loginButtonListener);


    }

    public OnClickListener loginButtonListener = new OnClickListener(){

        @Override
        public void onClick(View theView) {

            Intent getMainMenu = new Intent(LoginActivity.this, MainMenu.class);
            startActivity(getMainMenu);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
