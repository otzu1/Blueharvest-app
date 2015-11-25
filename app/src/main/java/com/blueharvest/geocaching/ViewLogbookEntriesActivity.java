package com.blueharvest.geocaching;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ViewLogbookEntriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_logbook_entries);
        // todo: populate listview of logbook entries
        // I would love to have a more "book-like" presentation ...
        // perhaps a turning page animation when the user swipes
        // this activity from one "page" to the next.
    }
}
