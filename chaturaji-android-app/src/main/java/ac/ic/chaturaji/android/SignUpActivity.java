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
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Haider
 */
public class SignUpActivity extends Activity {

    String emailString = "";
    String nicknameString = "";
    String passwordString = "";
    private View.OnClickListener createAccountButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View theView) {

            SharedPreferences settings = getSharedPreferences("main", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("sound", true);

            emailString = email.getText().toString();
            nicknameString = nickname.getText().toString();
            passwordString = password.getText().toString();

            editor.putString("email", emailString);
            editor.putString("password", passwordString);

            editor.commit();

            Intent createAccountIntent = new Intent(SignUpActivity.this, MainMenu.class);

            try {

                String state = new Register().execute("").get();
                System.out.println(state);

                switch (state) {
                    case "Success":
                        startActivity(createAccountIntent);
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
    EditText email;
    EditText nickname;
    EditText password;
    Button createAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sign_up);

        email = (EditText) findViewById(R.id.email_edit_text);
        nickname = (EditText) findViewById(R.id.nickname_edit_text);
        password = (EditText) findViewById(R.id.password_edit_text);
        createAccount = (Button) findViewById(R.id.create_account_button);
        createAccount.setOnClickListener(createAccountButtonListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class Register extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... info) {
            ChatuService chatuService = ChatuService.getInstance();
            String state = chatuService.createAccount(emailString, passwordString, nicknameString);

            if (state.equals("Success")) {
                state = chatuService.login(emailString, passwordString);
            }

            return state;
        }

    }
}