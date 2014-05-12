package ac.ic.chaturaji.android.test;

import ac.ic.chaturaji.android.LoginActivity;
import ac.ic.chaturaji.android.R;
import ac.ic.chaturaji.android.SignUpActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Haider on 12/05/14.
 */
public class SignupTest extends ActivityInstrumentationTestCase2<SignUpActivity> {

    private SignUpActivity mActivity;


    private Button SignUpButton;
    private TextView mainTitle;
    private EditText email_edittext;
    private EditText nickname_edittext;
    private EditText password_edittext;


    public SignupTest() {
        super(SignUpActivity.class);
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        setActivityInitialTouchMode(false);

        mActivity = getActivity();

        SignUpButton = (Button) mActivity.findViewById(R.id.create_account_button);
        mainTitle = (TextView) mActivity.findViewById(R.id.sign_up_title);
        email_edittext = (EditText) mActivity.findViewById(R.id.email_edit_text);
        nickname_edittext = (EditText) mActivity.findViewById(R.id.nickname_edit_text);
        password_edittext = (EditText) mActivity.findViewById(R.id.password_edit_text);


    }

    public void testPreconditions() {

        assertTrue(SignUpButton != null);
        assertTrue(mainTitle != null);
        assertTrue(email_edittext != null);
        assertTrue(nickname_edittext != null);
        assertTrue(password_edittext != null);

    }

}
