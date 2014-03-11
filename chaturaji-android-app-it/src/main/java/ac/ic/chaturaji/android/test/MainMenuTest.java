package ac.ic.chaturaji.android.test;

import ac.ic.chaturaji.android.*;
import android.annotation.TargetApi;
import android.app.Instrumentation;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;

/**
 * @author haider
 */
public class MainMenuTest extends ActivityInstrumentationTestCase2<MainMenu> {

    private MainMenu mActivity;

    private Button singlePlayer;
    private Button multi_player_button;
    private Button settings_button;
    private Button logout_button;

    public MainMenuTest() {
        super(MainMenu.class);
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        setActivityInitialTouchMode(false);

        mActivity = getActivity();

        singlePlayer = (Button) mActivity.findViewById(R.id.single_player_button);
        multi_player_button = (Button) mActivity.findViewById(R.id.multi_player_button);
        settings_button = (Button) mActivity.findViewById(R.id.settings_button);
        logout_button = (Button) mActivity.findViewById(R.id.log_out_button);
    }

    public void testPreconditions() {

        assertTrue(singlePlayer != null);
        assertTrue(multi_player_button != null);
        assertTrue(settings_button != null);
        assertTrue(logout_button != null);
    }

    public void testTClickSinglePlayer(){

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(ChooseColour.class.getName(), null, false);

        MainMenu myActivity = getActivity();

        final Button singlePlayer = (Button) myActivity.findViewById(R.id.single_player_button);

        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                singlePlayer.performClick();
            }
        });

        ChooseColour nextActivity = (ChooseColour) getInstrumentation().waitForMonitor(activityMonitor);

        assertNotNull(nextActivity);
        nextActivity.finish();

    }

    public void testTClickMultiPlayer(){

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(GameRoomActivity.class.getName(), null, false);

        MainMenu myActivity = getActivity();

        final Button mPlayer = (Button) myActivity.findViewById(R.id.multi_player_button);

        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mPlayer.performClick();
            }
        });

        GameRoomActivity nextActivity = (GameRoomActivity) getInstrumentation().waitForMonitor(activityMonitor);

        assertNotNull(nextActivity);
        nextActivity.finish();

    }

    public void testTClickSettings(){

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(SettingsActivity.class.getName(), null, false);

        MainMenu myActivity = getActivity();

        final Button settings = (Button) myActivity.findViewById(R.id.settings_button);

        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                settings.performClick();
            }
        });

        SettingsActivity nextActivity = (SettingsActivity) getInstrumentation().waitForMonitor(activityMonitor);

        assertNotNull(nextActivity);
        nextActivity.finish();

    }

    public void testTClickLogout(){

        Instrumentation.ActivityMonitor activityMonitor = getInstrumentation().addMonitor(LoginActivity.class.getName(), null, false);

        MainMenu myActivity = getActivity();

        final Button logout = (Button) myActivity.findViewById(R.id.log_out_button);

        myActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                logout.performClick();
            }
        });

        LoginActivity nextActivity = (LoginActivity) getInstrumentation().waitForMonitor(activityMonitor);

        assertNotNull(nextActivity);
        nextActivity.finish();

    }
}