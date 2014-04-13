package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChaturajiService;
import ac.ic.chaturaji.model.Game;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;

/**
 * @author Haider
 */
public class GameRoomActivity extends Activity {

    private static final String TAG = "GameRoomActivity";
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

    public void joinGameClickListener(View v) {

        String gameId = v.getTag().toString();

        Intent gotoGame = new Intent(GameRoomActivity.this, GameActivity.class);

        try {

            String[] state = new JoinGame().execute(gameId).get();
            Log.d(TAG, "JoinGame status " + Arrays.asList(state));

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
                case "401":
                    Toast.makeText(getApplicationContext(), "Unauthorized, perhaps your session has run out.", Toast.LENGTH_LONG).show();
                    Intent logOut = new Intent(GameRoomActivity.this, LoginActivity.class);
                    logOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ChaturajiService chaturajiService = ChaturajiService.getInstance();
                    chaturajiService.logout();
                    chaturajiService.clearCookieCred();
                    startActivity(logOut);
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

    private class GetGames extends AsyncTask<Void, Void, Game[]> {

        @Override
        protected Game[] doInBackground(Void... voids) {

            gamesList = ChaturajiService.getInstance().getGames();

            if (gameRooms != null && gamesList != null) {
                gameRooms.setAdapter(new GameRoomAdapter(GameRoomActivity.this, Arrays.asList(gamesList)));
            } else {
                Toast.makeText(getApplicationContext(), "Sorry, there was a problem connecting with server..", Toast.LENGTH_LONG).show();
            }

            Log.d(TAG, "Games list received from server " + Arrays.asList(gamesList));

            return gamesList;
        }

    }

    private class JoinGame extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... info) {
            return ChaturajiService.getInstance().joinGame(info[0]);
        }

    }
}
