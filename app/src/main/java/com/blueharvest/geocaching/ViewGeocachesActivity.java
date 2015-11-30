package com.blueharvest.geocaching;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity displays a list of Geocaches.
 * todo: handle no results
 * todo: incorporate more details for each item
 * todo: this activity is only an entry for list by location
 * and should also be flexible to handle other lists as well
 * (i.e. favorites and found geocaches)
 */
public class ViewGeocachesActivity extends AppCompatActivity {

    private double searchRadius = 0.0;
    private double searchLat = 0.0;
    private double searchLon = 0.0;

    ListView listView;
    List<RowItem> rowItems;

    private SearchTask mSearchTask = null;

    public void onBackPressed() {
        //Log.d(TAG, "onBackPressed");
        startActivity(new Intent(ViewGeocachesActivity.this, user_page.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_geocaches);
        // todo: populate listview with geocaches
        // this list could contain results from a search,
        // list view instead of map view, etc.

        // Get the passed search parameters
        searchRadius = getIntent().getDoubleExtra("SearchRad", 0.00);
        searchLat = getIntent().getDoubleExtra("SearchLat", 0.00);
        searchLon = getIntent().getDoubleExtra("SearchLong", 0.00);


        startSearchTask(searchLat, searchLon);

        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent displayCache = new Intent(ViewGeocachesActivity.this, ViewGeocacheActivity.class);

                String selected = ((TextView) view.findViewById(R.id.desc)).getText().toString();
                displayCache.putExtra("code", selected);
                startActivity(displayCache);
            }
        });

    }


    public void startSearchTask(Double lat, Double lon) {

        if (mSearchTask == null) {
            Location searchCenter = new Location("Newer");
            searchCenter.setLatitude(lat);
            searchCenter.setLongitude(lon);
            mSearchTask = new SearchTask();
            mSearchTask.execute(searchCenter);
        }

    }

    public class SearchTask extends AsyncTask<Location, Void, Void> {

        // jmb: added private object for onpostexecute 2015-11-24
        private blueharvest.geocaching.soap.objects.geocache.geocaches gs;

        @Override
        protected Void doInBackground(Location... locations) {

            for (int i = 0; i < locations.length; i++) { // should only be one
                double myLatitude = locations[i].getLatitude();
                double myLongitude = locations[i].getLongitude();
                // get the geocaches from around the location coordinates here
                gs = new blueharvest.geocaching.soap.objects.geocache.geocaches(
                        myLatitude, myLongitude, searchRadius);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            // Generate the markers on the map
            if (gs != null) {
                // Populate list view
                rowItems = new ArrayList<RowItem>();

                for (blueharvest.geocaching.soap.objects.geocache geocache : gs) {

                    RowItem item = new RowItem(geocache.getName(), geocache.getCode());
                    rowItems.add(item);
                }


                CustomListAdapter adapter = new CustomListAdapter(ViewGeocachesActivity.this,
                        R.layout.listview_item, rowItems);
                listView.setAdapter(adapter);


                Log.d("view geocaches activity", "Geocache found");

            } else {

                Log.d("view geocaches activity", "Geocache not found");

            }

            mSearchTask = null;
        }

        @Override
        protected void onCancelled() {
            mSearchTask = null;
        }
    }
}
