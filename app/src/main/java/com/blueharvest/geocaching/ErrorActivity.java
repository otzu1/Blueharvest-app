package com.blueharvest.geocaching;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Error Activity
 * displays a friendly error letting the user know something
 * didn't work out as we planned.
 * todo: receive error message to display
 */
public class ErrorActivity extends AppCompatActivity {

    public void onBackPressed() {
        startActivity(new Intent(ErrorActivity.this, user_page.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
    }

}
