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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * Created by Haider on 18/02/14.
 */
public class CreateGameActivity extends Activity {

    Button start_game_button;
    RadioGroup radioAI;
    RadioButton radioAI0;
    RadioButton radioAI1;
    RadioButton radioAI2;
    RadioButton radioAI3;

    String numberOfAI = "0";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.create_game);

        /* Following code done by Haider Qazi */

        start_game_button = (Button) findViewById(R.id.start_game_button);
        radioAI = (RadioGroup) findViewById(R.id.radioAI);
        radioAI0 = (RadioButton) findViewById(R.id.radioAI0);
        radioAI1 = (RadioButton) findViewById(R.id.radioAI1);
        radioAI2 = (RadioButton) findViewById(R.id.radioAI2);
        radioAI3 = (RadioButton) findViewById(R.id.radioAI3);

        start_game_button.setOnClickListener(startButtonListener);
        radioAI0.setOnClickListener(radioAI0Listener);
        radioAI1.setOnClickListener(radioAI1Listener);
        radioAI2.setOnClickListener(radioAI2Listener);
        radioAI3.setOnClickListener(radioAI3Listener);


    }

    public View.OnClickListener startButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View theView) {

            Intent startGame = new Intent(CreateGameActivity.this, GameActivity.class);

            PostGame postgame = new PostGame();

            try {

                postgame.execute(numberOfAI);
                String state = postgame.get();
                System.out.println(state);

                if(state.equals("Error")){
                    Toast.makeText(getApplicationContext(), "Sorry, there was a problem connecting with server..", Toast.LENGTH_LONG).show();
                }
                else{

                    startActivity(startGame);
                }

            }
            catch(Exception e){

                e.printStackTrace();
            }
        }
    };

    public View.OnClickListener radioAI0Listener = new View.OnClickListener(){

        @Override
        public void onClick(View theView) {

            numberOfAI = radioAI0.getText().toString();
        }
    };

    public View.OnClickListener radioAI1Listener = new View.OnClickListener(){

        @Override
        public void onClick(View theView) {

            numberOfAI = radioAI1.getText().toString();
        }
    };

    public View.OnClickListener radioAI2Listener = new View.OnClickListener(){

        @Override
        public void onClick(View theView) {

            numberOfAI = radioAI2.getText().toString();
        }
    };

    public View.OnClickListener radioAI3Listener = new View.OnClickListener(){

        @Override
        public void onClick(View theView) {

            numberOfAI = radioAI3.getText().toString();
        }
    };

    private class PostGame extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... AIs) {
            ChatuService chatuService = new ChatuService();
            String state = chatuService.createGame(AIs[0]);
            return state;
        }

    }
    /* End of Haider's code block */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
