package ac.ic.chaturaji.android;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class ac.ic.chaturaji.android.LoginActivityTest \
 * ac.ic.chaturaji.android.tests/android.test.InstrumentationTestRunner
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    public LoginActivityTest() {
        super("ac.ic.chaturaji.android", LoginActivity.class);
    }

}
