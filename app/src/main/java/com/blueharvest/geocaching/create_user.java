package com.blueharvest.geocaching;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class create_user extends AppCompatActivity {

    /**
     * Minimum length of a password.
     */
    private static final int minPasswordLen = 6;
    private static final int minUsernameLen = 6;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserCreateTask mCreateTask = null;


    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mConfirmPwdView;
    private EditText mEmailView;
    private View mProgressView;
    private View mCreateUserFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        mUsernameView = (EditText) findViewById(R.id.createUserName);
        mPasswordView = (EditText) findViewById(R.id.createUserPwd);
        mConfirmPwdView = (EditText) findViewById(R.id.createUserPwdConf);
        mEmailView = (EditText) findViewById(R.id.createUserEmail);
        Button mCreateUserButton = (Button) findViewById(R.id.createUserButton);
        mCreateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateUser();
            }
        });
        mCreateUserFormView = findViewById(R.id.CreateUsernameForm);

        mUsernameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > minUsernameLen) {
                    isUserNameValid();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mUsernameView.getText().toString().length() == 0) {
                    mUsernameView.setBackgroundColor(Color.WHITE);
                }
            }
        });
        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mPasswordView.length() > minPasswordLen && mConfirmPwdView.length() > minPasswordLen) {

                    doPasswordsMatch(mPasswordView.getText().toString(), mConfirmPwdView.getText().toString());

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mPasswordView.getText().toString().length() == 0) {
                    mPasswordView.setBackgroundColor(Color.WHITE);
                }
            }
        });

        mConfirmPwdView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mPasswordView.length() > minPasswordLen && mConfirmPwdView.length() > minPasswordLen) {

                    doPasswordsMatch(mPasswordView.getText().toString(), mConfirmPwdView.getText().toString());

                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mConfirmPwdView.getText().toString().length() == 0) {
                    mConfirmPwdView.setBackgroundColor(Color.WHITE);
                }
            }
        });
    }

    private boolean doPasswordsMatch(String password1, String password2) {

        if (password1.equals(password2)) {

            mPasswordView.setBackgroundColor(Color.GREEN);
            mConfirmPwdView.setBackgroundColor(Color.GREEN);
        }
        else {
            mConfirmPwdView.setBackgroundColor(Color.RED);
        }

        return password1.equals(password2);
    }

    private boolean isUsernameLenValid (String username) {

        return username.length() >= minUsernameLen;
    }
    private void isUserNameValid() {
        //TODO: Replace this with the username validation logic
        if( mUsernameView.getText().toString().length() > minPasswordLen) {
            mUsernameView.setBackgroundColor(Color.GREEN);
        }
        else {
            mUsernameView.setBackgroundColor(Color.RED);
        }
    }

    public void checkEmail() {
            String getText= mEmailView.getText().toString();
            String Expn =
                    "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                            +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                            +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                            +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                            +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

            if (getText.matches(Expn) && getText.length() > 0)
            {
                mEmailView.setBackgroundColor(Color.GREEN);
            }
            else
            {
                mEmailView.setBackgroundColor(Color.RED);
            }
        }

    private void attemptCreateUser() {


        // Get value in Username field
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confPwd = mConfirmPwdView.getText().toString();
        String email = mEmailView.getText().toString();

        if (mCreateTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mConfirmPwdView.setError(null);
        mEmailView.setError(null);

        // Reset Error Colors
        mUsernameView.setBackgroundColor(Color.WHITE);
        mPasswordView.setBackgroundColor(Color.WHITE);
        mConfirmPwdView.setBackgroundColor(Color.WHITE);
        mEmailView.setBackgroundColor(Color.WHITE);

        // Username
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError("This Field is Required");
            focusView = mUsernameView;
            cancel = true;
        }
        else if (username.length() < minUsernameLen){
            mUsernameView.setError("Username too short");
            mUsernameView.setBackgroundColor(Color.RED);
            focusView = mUsernameView;
            cancel = true;
        }

        // Password
        if (!cancel) {
            if (TextUtils.isEmpty(password)) {
                mPasswordView.setError("This Field is Required");
                focusView = mPasswordView;
                cancel = true;
            }
            if (password.length() < minPasswordLen) {
                mPasswordView.setError("Password too short");
                mPasswordView.setBackgroundColor(Color.RED);
                focusView = mPasswordView;
                cancel = true;
            }
        }

        // Find a punctuation character
        if (!cancel) {
            Pattern punct = Pattern.compile("[.!?\\-*@]");
            Matcher m = punct.matcher(password);
            boolean puncFound = m.find();
            if (!puncFound) {
                mPasswordView.setError("No punctuation character");
                mPasswordView.setBackgroundColor(Color.RED);
                focusView = mPasswordView;
                cancel = true;
            }
        }

        // Find a number
        if (!cancel) {
            Pattern number = Pattern.compile("\\d+");
            Matcher numberMatch = number.matcher(password);
            boolean numFound = numberMatch.find();
            if (!numFound) {
                mPasswordView.setError("No Digit (0-9) Character");
                mPasswordView.setBackgroundColor(Color.RED);
                focusView = mPasswordView;
                cancel = true;
            }
        }

        // Find an upper case character
        if (!cancel) {
            Pattern upper = Pattern.compile("[A-Z]");
            Matcher upperMatch = upper.matcher(password);
            boolean upperFound = upperMatch.find();
            if (!upperFound) {
                mPasswordView.setError("No uppercase character");
                mPasswordView.setBackgroundColor(Color.RED);
                focusView = mPasswordView;
                cancel = true;
            }
        }

        // Find a lower case character
        if (!cancel) {
            Pattern lower = Pattern.compile("[a-z]");
            Matcher lowerMatch = lower.matcher(password);
            boolean lowerFound = lowerMatch.find();
            if (!lowerFound) {
                mPasswordView.setError("No lowercase character");
                mPasswordView.setBackgroundColor(Color.RED);
                focusView = mPasswordView;
                cancel = true;
            }
        }

        // Verify passwords Match
        if (!cancel) {
            if (!password.equals(confPwd)) {
                mPasswordView.setError("Passwords Do Not Match");
                mPasswordView.setBackgroundColor(Color.RED);
                mConfirmPwdView.setBackgroundColor(Color.RED);
                focusView = mPasswordView;
                cancel = true;
            }
        }

        // Check Email
        if (!cancel) {
            if (!email.contains("@")) {
                mEmailView.setError("Missing Email");
                mEmailView.setBackgroundColor(Color.RED);
                focusView = mEmailView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            mCreateTask = new UserCreateTask(username, password, email);
            mCreateTask.execute((Void) null);
        }
    }

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String DUMMY_CREDENTIALS = "newUser1:TeStInG.1234:email@domain.com";

    public class UserCreateTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private final String mEmail;

        UserCreateTask (String username, String password, String email) {
            mEmail = email;
            mPassword = password;
            mUsername = username;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            String[] pieces = DUMMY_CREDENTIALS.split(":");
            return !(pieces[0].equals(mUsername) || pieces[2].equals(mEmail));
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mCreateTask = null;
            String error_user_exist = "Error User Already Exists";
            //showProgress(false);

            if (success) {
                finish();
            } else {
                mUsernameView.setError(error_user_exist);
                mUsernameView.setBackgroundColor(Color.RED);
                mUsernameView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mCreateTask = null;
            //showProgress(false);
        }
    }

}
