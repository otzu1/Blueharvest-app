package com.blueharvest.geocaching;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    public void onBackPressed() {
        //Log.d(TAG, "onBackPressed");
        startActivity(new Intent(SettingsActivity.this, user_page.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // todo: this feature is forthcoming
    }
}
