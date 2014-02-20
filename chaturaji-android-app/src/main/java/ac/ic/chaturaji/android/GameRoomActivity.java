package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChatuService;
import ac.ic.chaturaji.model.Player;
import ac.ic.chaturaji.model.PlayerType;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import ac.ic.chaturaji.model.Game;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Haider on 12/02/14.
 */

/* Following code mostly done by Haider Qazi - except for a few lines*/

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

        GetGames getgames = new GetGames();
        getgames.execute();

    }


    private class GetGames extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            ChatuService testService = new ChatuService();
            String test = testService.getGames();
            System.out.println(test);
            return test;
        }

        protected void onPostExecute(String test) {
            try {
                gamesList = new ObjectMapper().readValue(test, Game[].class);
                System.out.println(test);
                System.out.println(gamesList.toString());
            }
            catch (JsonGenerationException e) {
                Log.d("JsonGenerationException ",  e.toString());
                e.printStackTrace();

            } catch (JsonMappingException e) {
                Log.d("JsonMappingException", e.toString());
                e.printStackTrace();

            } catch (IOException e) {
                Log.d("IOException", e.toString());
                e.printStackTrace();

            }

            if(gameRooms != null && gamesList != null){
                gameRooms.setAdapter(new GameRoomAdapter(GameRoomActivity.this, Arrays.asList(gamesList)));}
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

    /* End of Haider's code block */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
