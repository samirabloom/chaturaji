package ac.ic.chaturaji.android.test;

import ac.ic.chaturaji.android.*;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Haider on 12/05/14.
 */
public class LoginTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private LoginActivity mActivity;

    private Button LoginButton;
    private Button SignUpButton;
    private Button UpdatePassword;
    private ImageView launcherImage;
    private TextView mainTitle;
    private TextView user_pass;
    private EditText email_login;
    private EditText pass_login;


    public LoginTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        setActivityInitialTouchMode(false);

        mActivity = getActivity();

        LoginButton = (Button) mActivity.findViewById(R.id.login_button);
        SignUpButton = (Button) mActivity.findViewById(R.id.signup_button);
        UpdatePassword = (Button) mActivity.findViewById(R.id.update_button);
        launcherImage = (ImageView) mActivity.findViewById(R.id.launcher_main);
        mainTitle = (TextView) mActivity.findViewById(R.id.login_title);
        user_pass = (TextView) mActivity.findViewById(R.id.user_pass_text);
        email_login = (EditText) mActivity.findViewById(R.id.email_login);
        pass_login = (EditText) mActivity.findViewById(R.id.password_login);

    }

    public void testPreconditions() {

        assertTrue(LoginButton != null);
        assertTrue(SignUpButton != null);
        assertTrue(UpdatePassword != null);
        assertTrue(launcherImage != null);
        assertTrue(mainTitle != null);
        assertTrue(user_pass != null);
        assertTrue(email_login != null);
        assertTrue(pass_login != null);
    }
}


