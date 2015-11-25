package com.blueharvest.geocaching;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ViewGeocachesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_geocaches);
        // todo: populate listview with geocaches
        // this list could contain results from a search,
        // list view instead of map view, etc.
    }
}
