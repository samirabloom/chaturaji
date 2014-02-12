package ac.ic.chaturaji.android;

import ac.ic.chaturaji.chatuService.ChatuService;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import ac.ic.chaturaji.model.Game;
import android.widget.ListView;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haider on 12/02/14.
 */
public class GameRoomActivity extends Activity {

    ListView gameRooms;
    List<Game> gamesList = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);
        setContentView(R.layout.game_rooms);

        gameRooms = (ListView) findViewById(R.id.game_rooms_list);

        // This is only for testing purposes in order to make the request
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ChatuService testService = new ChatuService();
        String test = testService.getGames();

        try {
            gamesList = new ObjectMapper().readValue(test, new TypeReference<List<Game>>(){});
        }
        catch (JsonGenerationException e) {

            e.printStackTrace();

        } catch (JsonMappingException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        Log.d("Request Test", test);
        // End of testing section

        if(gameRooms != null)
        gameRooms.setAdapter(new GameRoomAdapter(this, gamesList));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
