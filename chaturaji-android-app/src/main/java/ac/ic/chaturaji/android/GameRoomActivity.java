package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChatuService;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author Haider
 */
public class GameRoomActivity extends Activity {

    public View.OnClickListener createGameButtonListener = new View.OnClickListener() {

        @Override
        public void onClick(View theView) {

            Intent createGame = new Intent(GameRoomActivity.this, CreateGameActivity.class);
            startActivity(createGame);
        }
    };
    ListView gameRooms;
    Game[] gamesList;
    Button create_game_button;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.game_rooms);

        context = getApplicationContext();

        create_game_button = (Button) findViewById(R.id.create_game_button);
        create_game_button.setOnClickListener(createGameButtonListener);

        gameRooms = (ListView) findViewById(R.id.game_rooms_list);

        new GetGames().execute();

    }

    public void joinGameClickListener(View v){

        String gameId = v.getTag().toString();

        Intent gotoGame = new Intent(GameRoomActivity.this, GameActivity.class);

        try {

            String[] state = new JoinGame().execute(gameId).get();
            System.out.println(Arrays.asList(state));

            String colour = "in_game_yellow";

            switch (state[2]) {
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

            gotoGame.putExtra("colour", colour);

            switch (state[1]) {
                case "Error":
                    Toast.makeText(getApplicationContext(), "Sorry, there was a problem connecting with server.. " + state[0], Toast.LENGTH_LONG).show();
                    break;
                case "Success":
                    startActivity(gotoGame);
                    break;
                case "Bad request":
                    Toast.makeText(getApplicationContext(), state[0], Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class GetGames extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            ChatuService testService = ChatuService.getInstance();

            String test = testService.getGames();

            System.out.println(test);

            return test;
        }

        protected void onPostExecute(String test) {

            try {
                gamesList = new ObjectMapperFactory().createObjectMapper().readValue(test, Game[].class);
                System.out.println(test);
                System.out.println(Arrays.asList(gamesList));
            } catch (JsonGenerationException e) {
                Log.d("JsonGenerationException ", e.toString());
                e.printStackTrace();
            } catch (JsonMappingException e) {
                Log.d("JsonMappingException", e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("IOException", e.toString());
                e.printStackTrace();
            }

            if (gameRooms != null && gamesList != null) {

                gameRooms.setAdapter(new GameRoomAdapter(GameRoomActivity.this, Arrays.asList(gamesList)));

            } else

                Toast.makeText(getApplicationContext(), "Sorry, there was a problem connecting with server..", Toast.LENGTH_LONG).show();

        }

    }

    private class JoinGame extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... info) {
            return ChatuService.getInstance().joinGame(info[0]);
        }

    }
}
