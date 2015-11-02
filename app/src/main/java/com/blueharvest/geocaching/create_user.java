package com.blueharvest.geocaching;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
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

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isPasswordValid();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mConfirmPwdView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                doPasswordsMatch(mPasswordView.getText().toString(), mConfirmPwdView.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEmail();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private boolean isPasswordValid() {
        //TODO: Replace this with your own logic
        String password = mPasswordView.getText().toString();

        if (password.length() >= minPasswordLen) {
            mPasswordView.setBackgroundColor(Color.GREEN);
        }
        else {
            mPasswordView.setBackgroundColor(Color.RED);
        }

        return password.length() >= minPasswordLen;

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
    private boolean isUserNameValid(String username) {
        //TODO: Replace this with the username validation logic
        return username.length() > minPasswordLen;
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

        // Username
        if (username.length() < minUsernameLen){
            mUsernameView.setError("Username too short");
            mUsernameView.setBackgroundColor(Color.RED);
            focusView = mUsernameView;
            cancel = true;
        }

        // Password
        if (password.length() < minPasswordLen) {
            mPasswordView.setError("Password too short");
            mPasswordView.setBackgroundColor(Color.RED);
            focusView = mPasswordView;
            cancel = true;
        }

        Pattern punct = Pattern.compile("[.!?\\-*@]");
        Matcher m = punct.matcher(password);
        boolean puncFound = m.find();
        if (!puncFound) {
            mPasswordView.setError("No punctuation character");
            mPasswordView.setBackgroundColor(Color.RED);
            focusView = mPasswordView;
            cancel = true;
        }

        // Find a number
        Pattern number = Pattern.compile("\\d+");
        Matcher numberMatch = number.matcher(password);
        boolean numFound = numberMatch.find();
        if (!numFound) {
            mPasswordView.setError("No Digit (0-9) Character");
            mPasswordView.setBackgroundColor(Color.RED);
            focusView = mPasswordView;
            cancel = true;
        }

        // Find an upper case character
        Pattern upper = Pattern.compile("[A-Z]");
        Matcher upperMatch = upper.matcher(password);
        boolean upperFound = upperMatch.find();
        if (!upperFound) {
            mPasswordView.setError("No uppercase character");
            mPasswordView.setBackgroundColor(Color.RED);
            focusView = mPasswordView;
            cancel = true;
        }

        // Find a lower case character
        Pattern lower = Pattern.compile("[a-z]");
        Matcher lowerMatch = lower.matcher(password);
        boolean lowerFound = lowerMatch.find();
        if (!lowerFound) {
            mPasswordView.setError("No lowercase character");
            mPasswordView.setBackgroundColor(Color.RED);
            focusView = mPasswordView;
            cancel = true;
        }

        // Verify passwords Match
        if (!password.equals(confPwd)) {
            mPasswordView.setError("Passwords Do Not Match");
            mPasswordView.setBackgroundColor(Color.RED);
            mConfirmPwdView.setBackgroundColor(Color.RED);
            focusView = mPasswordView;
            cancel = true;
        }

        // Check Email
        if (!email.contains("@")) {
            mEmailView.setError("Missing Email");
            mEmailView.setBackgroundColor(Color.RED);
            focusView = mEmailView;
            cancel = true;
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
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

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

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mCreateTask = null;
            //showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mCreateTask = null;
            //showProgress(false);
        }
    }

}
