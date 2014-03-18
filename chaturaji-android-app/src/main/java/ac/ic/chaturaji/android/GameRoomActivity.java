package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChatuService;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import ac.ic.chaturaji.model.Game;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Haider on 12/02/14.
 */


public class GameRoomActivity extends Activity {

    ListView gameRooms;
    Game[] gamesList;
    Button create_game_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.game_rooms);

        create_game_button = (Button) findViewById(R.id.create_game_button);
        create_game_button.setOnClickListener(createGameButtonListener);

        gameRooms = (ListView) findViewById(R.id.game_rooms_list);
        gameRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String gameId = view.getTag().toString();

                JoinGame joinGame = new JoinGame();

                Intent gotoGame = new Intent(GameRoomActivity.this, GameActivity.class);


                try {

                    joinGame.execute(gameId);
                    String[] state = joinGame.get();
                    System.out.println(state);

                    String colour = "in_game_yellow";

                    if(state[2].equals("BLUE"))
                        colour = "in_game_blue";

                    else if(state[2].equals("RED"))
                        colour = "in_game_red";

                    else if(state[2].equals("GREEN"))
                        colour = "in_game_green";

                    gotoGame.putExtra("colour", colour);

                    if(state[1].equals("Error")){
                        Toast.makeText(getApplicationContext(), "Sorry, there was a problem connecting with server..", Toast.LENGTH_LONG).show();
                    }

                    else if(state[1].equals("Success")){
                        startActivity(gotoGame);
                    }

                    else if(state[1].equals("Bad request")) {

                        Toast.makeText(getApplicationContext(), state[0], Toast.LENGTH_LONG).show();

                    }

                }
                catch(Exception e){

                    e.printStackTrace();
                }

            }
        });


        GetGames getgames = new GetGames();
        getgames.execute();

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
                System.out.println(gamesList.toString());
            }

            catch (JsonGenerationException e) {
                Log.d("JsonGenerationException ",  e.toString());
                e.printStackTrace();

            }

            catch (JsonMappingException e) {
                Log.d("JsonMappingException", e.toString());
                e.printStackTrace();

            }

            catch (IOException e) {
                Log.d("IOException", e.toString());
                e.printStackTrace();

            }

            if(gameRooms != null && gamesList != null){

                gameRooms.setAdapter(new GameRoomAdapter(GameRoomActivity.this, Arrays.asList(gamesList)));

            }

            else

                Toast.makeText(getApplicationContext(), "Sorry, there was a problem connecting with server..", Toast.LENGTH_LONG).show();

        }

    }

    public View.OnClickListener createGameButtonListener = new View.OnClickListener(){

        @Override
        public void onClick(View theView) {

            Intent createGame = new Intent(GameRoomActivity.this, CreateGameActivity.class);
            startActivity(createGame);
        }
    };

    private class JoinGame extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... info) {
            ChatuService chatuService = ChatuService.getInstance();

            String[] state = chatuService.joinGame(info[0]);

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
