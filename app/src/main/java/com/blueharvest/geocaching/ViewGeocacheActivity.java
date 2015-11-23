package com.blueharvest.geocaching;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import blueharvest.geocaching.concepts.location;

/**
 * View Geocache Activity<br>
 * View of a geocache with a map to of the latitude and longitude coordinates. The geocache
 * attributes are all shown on this activity. The "code" message MUST be sent from the
 * preceding activity to function properly ... without it, the activity will fail.
 *
 * @since 2015-11-22
 */
public class ViewGeocacheActivity extends FragmentActivity {

    // for logging
    public static final String TAG = "blueharvest:: " + AddGeocacheActivity.class.getSimpleName();

    // map
    private GoogleMap map;
    private final static int MY_LOCATION_PERMISSION = 1;
    private final static double distance = 10; // km
    private blueharvest.geocaching.soap.objects.geocache.geocaches geocaches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_geocache);

        // map
        setUpMap();

        // get the geocache and set up the view in the postexecute function
        // todo: change after testing
        //new GeocacheTask().execute(getIntent().getStringExtra("code"));
        new GeocacheTask().execute("BH13GC7");

        // save button with click listener
        /*Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // todo: do something
            }
        });*/
        /*Dialog dialog = GooglePlayServicesUtil.getErrorDialog(0, this, 0);
        dialog.show();*/
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpMap();
                } else {

                }
            }
        }
    }

    public void setUpMap() {
        // Getting reference to the SupportMapFragment of  the layout .xml
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        // Getting GoogleMap object from the fragment
        map = fm.getMap();
        // Enable the Zoom Controls of the Google Map
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(true);
    }

    /**
     * leaving this here in case we ever want to show neighboring geocaches
     * @see <a href="http://developer.android.com/reference/android/os/AsyncTask.html">AsycncTask</a>
     * Params, Progress, Result
     * @since 2015-11-20
     */
    public class GeocachesTask extends AsyncTask<Location, Integer, Void> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param locations The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Location... locations) {
            for (int i = 0; i < locations.length; i++) { // should only be one
                double myLatitude = locations[i].getLatitude();
                double myLongitude = locations[i].getLongitude();
                // get the geocaches from around the location coordinates here
                geocaches = new blueharvest.geocaching.soap.objects.geocache.geocaches(
                        myLatitude, myLongitude, distance);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Long result) {
            //showDialog("something");
        }

    }

    /**
     * @see <a href="http://developer.android.com/reference/android/os/AsyncTask.html">AsycncTask</a>
     * Params, Progress, Result
     * @since 2015-11-22
     */
    public class GeocacheTask extends AsyncTask<String, Void, Void> {

        private blueharvest.geocaching.concepts.geocache g;

        protected Void doInBackground(String... params) {
            Log.d(TAG, params[0] + " was sent to the background task");
            g = blueharvest.geocaching.soap.objects.geocache.get(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            map.moveCamera(CameraUpdateFactory.newLatLng(
                    new LatLng(g.getLocation().getLatitude().getDecimalDegrees(),
                            g.getLocation().getLongitude().getDecimalDegrees())));
            // Zoom in the Google Map
            map.animateCamera(CameraUpdateFactory.zoomTo(15));
            // Geocache Latitude and Longitude
            // Current location marker
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(g.getLocation().getLatitude().getDecimalDegrees(),
                            g.getLocation().getLongitude().getDecimalDegrees()))
                    .title(g.getName()));
            /*((EditText) findViewById(R.id.latitude)).setText(
                    g.getLocation().getLatitude().toSexigesimal(
                            location.coordinate.type.latitude));
            ((EditText) findViewById(R.id.longitude)).setText(
                    g.getLocation().getLongitude().toSexigesimal(
                            location.coordinate.type.longitude));*/
        }

    }


}
