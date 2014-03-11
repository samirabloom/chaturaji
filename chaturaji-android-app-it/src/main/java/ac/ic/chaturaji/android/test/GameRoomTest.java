package ac.ic.chaturaji.android.test;

import ac.ic.chaturaji.android.GameRoomActivity;
import ac.ic.chaturaji.android.GameRoomAdapter;
import ac.ic.chaturaji.android.R;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ListView;

/**
 * Created by Haider on 10/03/14.
 */
public class GameRoomTest extends ActivityInstrumentationTestCase2<GameRoomActivity> {

    private GameRoomActivity mActivity;

    private GameRoomAdapter localAdapter;

    private ListView localListView;

    public GameRoomTest() {
        super(GameRoomActivity.class);
    }


    @Override
    protected void setUp() throws Exception {

        super.setUp();

        setActivityInitialTouchMode(false);

        mActivity = getActivity();

        localListView = (ListView) mActivity.findViewById(R.id.game_rooms_list);

        localAdapter = (GameRoomAdapter) localListView.getAdapter();
    }

    public void testPreconditions() {

        assertTrue(localListView.getOnItemClickListener() != null);

        assertTrue(localAdapter == null);

    }

    public void testList(){

        mActivity.onCreate(new Bundle());

        localListView = (ListView) mActivity.findViewById(R.id.game_rooms_list);

        localAdapter = (GameRoomAdapter) localListView.getAdapter();
    }

}
