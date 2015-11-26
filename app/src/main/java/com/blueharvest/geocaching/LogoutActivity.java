package com.blueharvest.geocaching;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        // todo: clear the back stack

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(3000); // 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // clear the user identifier preference (that one only)
                    // and redirect the user to log in.
                    SharedPreferences pref // 0 - for private mode
                            = getApplicationContext().getSharedPreferences("blueharvest", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.remove("me");
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }
}
