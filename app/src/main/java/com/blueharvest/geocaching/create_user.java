package com.blueharvest.geocaching;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class create_user extends AppCompatActivity {

    /**
     * Minimum length of a password.
     */
    private static final int minPasswordLen = 6;

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

    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= minPasswordLen;
    }

    private boolean isUserNameValid(String username) {
        //TODO: Replace this with the username validation logic
        return username.length() > minPasswordLen;
    }

    private boolean attemptCreateUser() {

        // Get value in Username field

        return false;

    }

}
