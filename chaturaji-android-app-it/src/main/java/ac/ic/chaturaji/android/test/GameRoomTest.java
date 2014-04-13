package ac.ic.chaturaji.android.test;

import ac.ic.chaturaji.android.CreateGameActivity;
import ac.ic.chaturaji.android.GameRoomActivity;
import ac.ic.chaturaji.android.GameRoomAdapter;
import ac.ic.chaturaji.android.R;
import ac.ic.chaturaji.model.Game;
import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.ListView;

import java.util.Arrays;

/**
 * @author Haider
 */
public class GameRoomTest extends ActivityInstrumentationTestCase2<GameRoomActivity> {

    private String mockGameList = "[     {         \"id\": \"d4baf199-d347-4351-a7be-9417dea7dcab\",         \"players\": [             {                 \"id\": \"f8c558ca-bf43-404d-a511-85f524e1028a\",                 \"gameId\": \"d4baf199-d347-4351-a7be-9417dea7dcab\",                 \"type\": \"HUMAN\",                 \"colour\": \"YELLOW\",                 \"user\": {                     \"id\": \"fa92ca9c-b5fa-4767-a621-d81b963331a2\",                     \"nickname\": \"testman\",                     \"email\": \"test@test.com\"                 },                 \"points\": 0             }         ],         \"currentPlayer\": \"YELLOW\",         \"playerCount\": 1,         \"createdDate\": [             2014,             3,             11,             0,             10,             0,             0         ]     },     {         \"id\": \"26366f7b-97d3-45ef-92af-5e8d911a3341\",         \"players\": [             {                 \"id\": \"0f3c1b99-d945-4532-8cb8-ea97c27a8412\",                 \"gameId\": \"26366f7b-97d3-45ef-92af-5e8d911a3341\",                 \"type\": \"HUMAN\",                 \"colour\": \"YELLOW\",                 \"user\": {                     \"id\": \"fa92ca9c-b5fa-4767-a621-d81b963331a2\",                     \"nickname\": \"testman\",                     \"email\": \"test@test.com\"                 },                 \"points\": 0             }         ],         \"currentPlayer\": \"YELLOW\",         \"playerCount\": 1,         \"createdDate\": [             2014,             3,             11,             0,             11,             0,             0         ]     },     {         \"id\": \"002f8596-2abf-4e68-b5e9-028768a3c4f2\",         \"players\": [             {                 \"id\": \"568f6f90-0784-4b36-81f1-dd15a27828e6\",                 \"gameId\": \"002f8596-2abf-4e68-b5e9-028768a3c4f2\",                 \"type\": \"HUMAN\",                 \"colour\": \"YELLOW\",                 \"user\": {                     \"id\": \"c0052275-1f0e-477f-8992-ab4cd9231d93\",                     \"nickname\": \"testman\",                     \"email\": \"test@test.com\"                 },                 \"points\": 0             },             {                 \"id\": \"59c9869b-3d7c-4cd3-8ea4-d3a73b40d1de\",                 \"gameId\": \"002f8596-2abf-4e68-b5e9-028768a3c4f2\",                 \"type\": \"AI\",                 \"colour\": \"BLUE\",                 \"user\": {                     \"id\": \"c0052275-1f0e-477f-8992-ab4cd9231d93\",                     \"nickname\": \"testman\",                     \"email\": \"test@test.com\"                 },                 \"points\": 0             },             {                 \"id\": \"e39faa73-7c28-447c-85a9-5f65328bb850\",                 \"gameId\": \"002f8596-2abf-4e68-b5e9-028768a3c4f2\",                 \"type\": \"AI\",                 \"colour\": \"RED\",                 \"user\": {                     \"id\": \"c0052275-1f0e-477f-8992-ab4cd9231d93\",                     \"nickname\": \"testman\",                     \"email\": \"test@test.com\"                 },                 \"points\": 0             }         ],         \"currentPlayer\": \"YELLOW\",         \"playerCount\": 3,         \"createdDate\": [             2014,             3,             11,             0,             10,             0,             0         ]     }]";

    private Game[] gamesList;
    private GameRoomActivity mActivity;

    private GameRoomAdapter localAdapter;

    private ListView localListView;

    public GameRoomTest() {
        super(GameRoomActivity.class);
    }


    @Override
    protected void setUp() throws Exception {

        setActivityInitialTouchMode(false);

        mActivity = getActivity();

        gamesList = new ObjectMapperFactory().createObjectMapper().readValue(mockGameList, Game[].class);

        localListView = (ListView) mActivity.findViewById(R.id.game_rooms_list);

        localAdapter = new GameRoomAdapter(mActivity, Arrays.asList(gamesList));
    }

    public void testTCreateGame() {

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(CreateGameActivity.class.getName(), null, false);

        GameRoomActivity myActivity = getActivity();

        final Button createGame = (Button) myActivity.findViewById(R.id.create_game_button);

        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createGame.performClick();
            }
        });

        CreateGameActivity nextActivity = (CreateGameActivity) getInstrumentation().waitForMonitor(activityMonitor);

        assertNotNull(nextActivity);
        nextActivity.finish();

    }


}
