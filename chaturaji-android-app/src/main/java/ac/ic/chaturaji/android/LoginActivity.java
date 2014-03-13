package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChatuService;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Haider on 12/02/14.
 */
public class LoginActivity extends Activity {

    Button login_button;
    Button signup_button;
    EditText email_edittext;
    EditText password_edittext;

    String email ="";
    String password ="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        login_button = (Button) findViewById(R.id.login_button);
        signup_button = (Button) findViewById(R.id.signup_button);
        email_edittext = (EditText) findViewById(R.id.email_login);
        password_edittext = (EditText) findViewById(R.id.password_login);

        login_button.setOnClickListener(loginButtonListener);
        signup_button.setOnClickListener(signupButtonListener);


    }

    public OnClickListener loginButtonListener = new OnClickListener(){

        @Override
        public void onClick(View theView) {

            SharedPreferences settings = getSharedPreferences("main", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("sound", true);

            email = email_edittext.getText().toString();
            password = password_edittext.getText().toString();

            editor.putString("email", email);
            editor.putString("password", password);

            editor.commit();

            Intent getMainMenu = new Intent(LoginActivity.this, MainMenu.class);
            PostGame postgame = new PostGame();

            try {

                postgame.execute("");
                String state = postgame.get();
                System.out.println(state);

                if(state.equals("Error")){
                    Toast.makeText(getApplicationContext(), "Sorry, there was a problem connecting with server..", Toast.LENGTH_LONG).show();
                }

                else if(state.equals("Invalid")){
                    Toast.makeText(getApplicationContext(), "There was a problem logging in. Have you entered the correct details?", Toast.LENGTH_LONG).show();
                }

                else{

                    startActivity(getMainMenu);
                }

            }
            catch(Exception e){

                e.printStackTrace();
            }
        }
    };

    public OnClickListener signupButtonListener = new OnClickListener(){

        @Override
        public void onClick(View theView) {

            Intent getSignup = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(getSignup);
        }
    };

    private class PostGame extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... info) {
            ChatuService chatuService = ChatuService.getInstance();

            String state = chatuService.login(email, password);

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
