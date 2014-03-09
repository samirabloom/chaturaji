package ac.ic.chaturaji.android.test;

import ac.ic.chaturaji.android.MainMenu;
import android.annotation.TargetApi;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;

public class HelloAndroidActivityTest extends ActivityInstrumentationTestCase2<MainMenu> {

    public HelloAndroidActivityTest() {
        super(MainMenu.class);
    }

    public void testActivity() {
        MainMenu activity = getActivity();
        assertNotNull(activity);
    }
}

