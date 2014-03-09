package ac.ic.chaturaji.android.test;

import ac.ic.chaturaji.android.MainMenu;
import ac.ic.chaturaji.android.R;
import android.annotation.TargetApi;
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
}