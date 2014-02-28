package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChatuService;
import android.app.Activity;
import android.content.Intent;
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
 * Created by Haider on 26/02/14.
 */


public class SignUpActivity extends Activity {

    String emailString = "";
    String nicknameString = "";
    String passwordString = "";
    EditText email;
    EditText nickname;
    EditText password;
    Button createAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.sign_up);

        email = (EditText)findViewById(R.id.email_edit_text);
        nickname = (EditText)findViewById(R.id.nickname_edit_text);
        password = (EditText)findViewById(R.id.password_edit_text);

        createAccount = (Button)findViewById(R.id.create_account_button);

        createAccount.setOnClickListener(createAccountButtonListener);


    }

    private View.OnClickListener createAccountButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View theView) {

            emailString = email.getText().toString();
            nicknameString = nickname.getText().toString();
            passwordString = password.getText().toString();

            Intent createAccountIntent = new Intent(SignUpActivity.this, MainMenu.class);
            PostGame postgame = new PostGame();

            try {

                postgame.execute("");
                String state = postgame.get();
                System.out.println(state);

                if(state.equals("Error")){
                    Toast.makeText(getApplicationContext(), "Sorry, there was a problem connecting with server..", Toast.LENGTH_LONG).show();
                }

                else if(state.equals("Invalid")){
                    Toast.makeText(getApplicationContext(), "Sorry, there was a problem logging in.", Toast.LENGTH_LONG).show();
                }

                else{

                    startActivity(createAccountIntent);
                }

            }
            catch(Exception e){

                e.printStackTrace();
            }

        }
    };

    private class PostGame extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... info) {
            ChatuService chatuService = new ChatuService();
            String state = chatuService.createAccount(emailString, passwordString, nicknameString);

            if(state.equals("Success"))
                state = chatuService.login(emailString, passwordString);

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