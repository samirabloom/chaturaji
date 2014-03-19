package ac.ic.chaturaji.android.test;

import ac.ic.chaturaji.android.R;
import ac.ic.chaturaji.android.SettingsActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * @author Haider
 */
public class SettingsActivityTest extends ActivityInstrumentationTestCase2<SettingsActivity> {

    private SettingsActivity mActivity;

    private CheckBox soundcbx;
    private Button changePass;
    private EditText password;

    public SettingsActivityTest() {
        super(SettingsActivity.class);
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        setActivityInitialTouchMode(false);

        mActivity = getActivity();

        soundcbx = (CheckBox) mActivity.findViewById(R.id.soundbox);

        changePass = (Button) mActivity.findViewById(R.id.updateButton);

        password = (EditText) mActivity.findViewById(R.id.newPassword);
    }

    public void testPreconditions() {

        assertTrue(soundcbx != null);

        assertTrue(changePass != null);

        assertTrue(password != null);

    }
}
