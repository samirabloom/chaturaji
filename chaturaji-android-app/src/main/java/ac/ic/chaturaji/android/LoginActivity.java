package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChaturajiService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Haider
 */
public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";
    public OnClickListener signupButtonListener = new OnClickListener() {

        @Override
        public void onClick(View theView) {

            SharedPreferences settings = getSharedPreferences("main", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("sound", false);
            editor.commit();

            Intent getSignup = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(getSignup);
        }
    };
    Button login_button;
    Button signup_button;
    Button update_button;
    EditText email_edittext;
    EditText password_edittext;
    String email = "";
    public OnClickListener updatePasswordButtonListener = new OnClickListener() {

        @Override
        public void onClick(View theView) {

            SharedPreferences settings = getSharedPreferences("main", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("sound", false);

            email = email_edittext.getText().toString();

            editor.putString("email", email);

            editor.commit();

            if (StringUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Please provide your email address..", Toast.LENGTH_LONG).show();
            } else {

                try {

                    String state = new UpdatePassword().execute(email).get();
                    Log.d(TAG, "UpdatePassword status " + state);

                    switch (state) {
                        case "Success":
                            Toast.makeText(getApplicationContext(), "An email has been sent with instructions on how to reset your password..", Toast.LENGTH_LONG).show();
                            break;
                        case "Error":
                            Toast.makeText(getApplicationContext(), "Sorry, there was a problem connecting with server..", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), state, Toast.LENGTH_LONG).show();
                            break;
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
    };
    String password = "";
    public OnClickListener loginButtonListener = new OnClickListener() {

        @Override
        public void onClick(View theView) {

            SharedPreferences settings = getSharedPreferences("main", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("sound", false);

            email = email_edittext.getText().toString();
            password = password_edittext.getText().toString();

            editor.putString("email", email);
            editor.putString("password", password);

            editor.commit();

            Intent getMainMenu = new Intent(LoginActivity.this, MainMenu.class);

            try {

                String state = new Login().execute("").get();
                Log.d(TAG, "Login status " + state);

                switch (state) {
                    case "Success":
                        getMainMenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(getMainMenu);
                        break;
                    case "Error":
                        Toast.makeText(getApplicationContext(), "Sorry, there was a problem connecting with server..", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), state, Toast.LENGTH_LONG).show();
                        break;
                }

            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        AudioManager aMan = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int amStreamMusicMaxVol = aMan.getStreamMaxVolume(aMan.STREAM_MUSIC);
        aMan.setStreamVolume(aMan.STREAM_MUSIC, amStreamMusicMaxVol, 0);

        login_button = (Button) findViewById(R.id.login_button);
        signup_button = (Button) findViewById(R.id.signup_button);
        update_button = (Button) findViewById(R.id.update_button);
        email_edittext = (EditText) findViewById(R.id.email_login);
        password_edittext = (EditText) findViewById(R.id.password_login);

        login_button.setOnClickListener(loginButtonListener);
        signup_button.setOnClickListener(signupButtonListener);
        update_button.setOnClickListener(updatePasswordButtonListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class Login extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... info) {
            return ChaturajiService.getInstance().login(email, password);
        }

    }

    private class UpdatePassword extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... info) {
            return ChaturajiService.getInstance().updatePassword(email);
        }

    }
}
