package com.blueharvest.geocaching;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Add Geocache Activity<br>
 * Creates a new geocache with a map to choose latitude and longitude coordinates, defaults
 * to current location, and allows for manually entering a latitude and longitude. If the user
 * decides to enter a location's latitude and longitude, the map then displays said location.
 * The name of the geocache is specified and the control includes spell checker and proper case
 * for every word; however, the user may specify a new word in any capitalization for those
 * functions are merely suggestions. The description of the geocache may contain up to
 * 4000 characters, includes a spell checker, and proper case by sentence (optional).
 * The code is an arbitrary string of alphanumeric characters beginning with 'GEO' and defaults to
 * a suggested random code (blueharvest-0.0.3+ required). Spinner controls are used for enumerating
 * values for size, terrain, and difficulty. The values for the enumerations are found hard-coded
 * in each respective .xml resource file in a string array and accessible through an adapter.
 * <p>
 * Eventually, this activity is to direct the user to the geocache details page not yet started.
 *
 * @see <a href="http://developer.android.com/guide/topics/ui/controls/spinner.html">
 * Populate the Spinner with User Choices</a>
 * @since 2015-11-19
 */
public class AddGeocacheActivity extends FragmentActivity
        implements LocationProvider.LocationCallback {

    // for logging
    public static final String TAG = "blueharvest:: " + AddGeocacheActivity.class.getSimpleName();

    private LocationProvider locationProvider;
    private GoogleMap map;
    private blueharvest.geocaching.soap.objects.geocache.geocaches geocaches;
    private final static double distance = 10; // km
    // coordinates in decimal degrees, +/- to specify bearing, up to 7 decimal places, ranged
    // see http://www.regexlib.com/REDetails.aspx?regexp_id=1535
    private EditText latitude; // ^-?([1-8]?[1-9]|[1-9]0)\.{1}\d{1,7}
    // see http://www.regexlib.com/REDetails.aspx?regexp_id=1536
    private EditText longitude; // ^-?([1]?[1-7][1-9]|[1]?[1-8][0]|[1-9]?[0-9])\.{1}\d{1,7}
    private EditText name;
    private EditText description;
    private EditText code;
    // http://developer.android.com/guide/topics/ui/controls/spinner.html
    private Spinner size;
    private Spinner terrain;
    private Spinner difficulty;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geocache);

        // map! todo: fixme
        //setUpMapIfNeeded();
        //locationProvider = new LocationProvider(this, this);
        // give the user the ability to choose location on the map.
        // figure it out!!

        // context controls
        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);
        code = (EditText) findViewById(R.id.code);
        size = (Spinner) findViewById(R.id.size);
        //String[] sizes = getResources().getStringArray(R.array.geocache_sizes);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(this,
                R.array.geocache_sizes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        size.setAdapter(sizeAdapter);
        terrain = (Spinner) findViewById(R.id.terrain);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> terrainAdapter = ArrayAdapter.createFromResource(this,
                R.array.geocache_terrain, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        terrainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        terrain.setAdapter(terrainAdapter);
        difficulty = (Spinner) findViewById(R.id.difficulty);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(this,
                R.array.geocache_difficulty, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        difficulty.setAdapter(difficultyAdapter);
        // save button with click listener
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something when the save button is clicked.
            }
        });
        // todo: to go to another activity ...
        // startActivity(new Intent(LoginActivity.this, create_user.class));

        /*Dialog dialog = GooglePlayServicesUtil.getErrorDialog(0, this, 0);
        dialog.show();*/
    }

    // todo: fixme
    //@Override
    /*protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        locationProvider.connect();
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        locationProvider.disconnect();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #map} is not null.
     * <p>
     * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    /*private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }*/

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #map} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        Log.d(TAG, "setUpMap()");
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
    }

    @Override
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        blueharvest.geocaching.util.GeoLocation[] b
                = blueharvest.geocaching.util.GeoLocation.fromDegrees(
                location.getLatitude(), location.getLongitude())
                .boundingCoordinates(distance, 6371.01); // distance & earth's radius in km
        LatLngBounds here = new LatLngBounds(
                new LatLng(b[0].getLatitudeInDegrees(), b[0].getLongitudeInDegrees()),
                new LatLng(b[1].getLatitudeInDegrees(), b[1].getLongitudeInDegrees()));
        // Set the camera to the greatest possible zoom level that includes the bounds
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(here, 0));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here.getCenter(), 10));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f)); // lower = further out

        Circle circle = map.addCircle(new CircleOptions()
                .center(here.getCenter())
                        // http://www.rapidtables.com/web/color/RGB_Color.htm
                .fillColor(Color.argb(0x55, 0xd2, 0xb4, 0x8c)) // 85, 210, 180, 140
                .strokeColor(Color.argb(0xaa, 0xd2, 0xb4, 0x8c)) // 170, 210, 180, 140
                .radius(distance * 1000) // meters (distance is km)
                .strokeWidth(3f)); // pixels

        // current location marker
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("I am here!"); // todo: change title
        map.addMarker(options);

        new GeocachesTask().execute(location);
        if (geocaches != null) { // null check
            for (blueharvest.geocaching.soap.objects.geocache g : geocaches) {
                map.addMarker(new MarkerOptions().position(
                        new LatLng(g.getLocation().getLatitude().getDecimalDegrees(),
                                g.getLocation().getLongitude().getDecimalDegrees()))
                        .title(g.getName()));
            }
        }
    }

    /**
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

}
