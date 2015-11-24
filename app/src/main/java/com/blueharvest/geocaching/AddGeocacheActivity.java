package com.blueharvest.geocaching;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
 * <p/>
 * This activity then directs the usee to the view geocache activity.
 * todo: validation, handle no location, etc.
 *
 * @see <a href="http://developer.android.com/guide/topics/ui/controls/spinner.html">
 * Populate the Spinner with User Choices</a>
 * @since 2015-11-19
 */
public class AddGeocacheActivity extends FragmentActivity implements LocationListener {

    // for logging
    public static final String TAG = "blueharvest:: " + AddGeocacheActivity.class.getSimpleName();

    // map
    private final static int MY_LOCATION_PERMISSION = 1;
    private final static double distance = 10; // km
    private GoogleMap map;
    private blueharvest.geocaching.soap.objects.geocache.geocaches geocaches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geocache);

        // map including default latitude and longitude
        setUpMap();

        // type spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.geocache_types, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ((Spinner) findViewById(R.id.type)).setAdapter(typeAdapter);

        // size spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(this,
                R.array.geocache_sizes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ((Spinner) findViewById(R.id.size)).setAdapter(sizeAdapter);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> terrainAdapter = ArrayAdapter.createFromResource(this,
                R.array.geocache_terrain, android.R.layout.simple_spinner_item);
        // terrain spinner
        // Specify the layout to use when the list of choices appears
        terrainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ((Spinner) findViewById(R.id.terrain)).setAdapter(terrainAdapter);
        // difficulty spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(this,
                R.array.geocache_difficulty, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        difficultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ((Spinner) findViewById(R.id.difficulty)).setAdapter(difficultyAdapter);

        // save button with click listener
        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get user identifier from shared preferences
                String me = getApplicationContext().getSharedPreferences(
                        "blueharvest", MODE_PRIVATE).getString("me", null);
                if (me != null) {
                    // wrap the controls in a geocache object and
                    // send it to the background thread to add the geocache
                    new AddGeocacheTask().execute(Geocache(java.util.UUID.fromString(me)));
                    // todo: handle problems with insert
                    /*if (the-insert-failed) {
                        // todo: do something, there's a problem
                    } else {
                        // todo: take the user to see the newly added geocache (like below)
                    }*/
                    Intent intent = new Intent(AddGeocacheActivity.this, ViewGeocacheActivity.class);
                    intent.putExtra("code", ((EditText) findViewById(R.id.code)).getText().toString());
                    startActivity(intent);
                } else {
                    // todo: something more elegant for the user
                    System.out.println("me is null. help!");
                }
            }
        });
        /*Dialog dialog = GooglePlayServicesUtil.getErrorDialog(0, this, 0);
        dialog.show();*/
    }

    /**
     * transforms the controls into a geocache object
     * todo: v 0.0.3 has a less complex insert to use
     *
     * @param userid
     * @return a geocache to add
     * @since 2015-11-22
     */
    private blueharvest.geocaching.soap.objects.geocache Geocache(java.util.UUID userid) {
        return new blueharvest.geocaching.soap.objects.geocache(
                null, null,
                ((EditText) findViewById(R.id.code)).getText().toString(),
                ((EditText) findViewById(R.id.name)).getText().toString(),
                ((EditText) findViewById(R.id.description)).getText().toString(),
                ((Spinner) findViewById(R.id.difficulty)).getSelectedItemPosition(),
                ((Spinner) findViewById(R.id.size)).getSelectedItemPosition(),
                ((Spinner) findViewById(R.id.terrain)).getSelectedItemPosition(),
                1, // status
                ((Spinner) findViewById(R.id.type)).getSelectedItemPosition(),
                new blueharvest.geocaching.soap.objects.user(
                        userid, null, null, null, null, null,
                        true, false, null, null, null),
                null,
                new blueharvest.geocaching.soap.objects.location(
                        null, ((EditText) findViewById(R.id.name)).getText().toString(),
                        Double.valueOf(((EditText) findViewById(R.id.latitude)).getText().toString()),
                        Double.valueOf(((EditText) findViewById(R.id.longitude)).getText().toString()),
                        3, null),
                null);
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
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        } else {  // Google Play Services are available
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            // Getting GoogleMap object from the fragment
            map = fm.getMap();
            // Enable MyLocation of the Google Map
            map.setMyLocationEnabled(true);
            // Enable the Zoom Controls of the Google Map
            map.getUiSettings().setZoomControlsEnabled(true);
            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // Getting Current Location
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    onLocationChanged(location);
                }
                locationManager.requestLocationUpdates(provider, 20000, 0, this);
            }
        }
    }

    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        // Showing the current location in Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(location.getLatitude(), location.getLongitude())));
        // Zoom in the Google Map
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        // Geocache Latitude and Longitude
        ((EditText) findViewById(R.id.latitude)).setText(String.valueOf(
                new java.text.DecimalFormat("##0.#######").format(location.getLatitude())));
        ((EditText) findViewById(R.id.longitude)).setText(String.valueOf(
                new java.text.DecimalFormat("##0.#######").format(location.getLongitude())));
        // Current location marker
        map.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .draggable(true)
                .title("New Geocache Marker!"));
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                ((EditText) findViewById(R.id.latitude)).setText(String.valueOf(
                        new java.text.DecimalFormat("##0.#######").format(
                                marker.getPosition().latitude)));
                ((EditText) findViewById(R.id.longitude)).setText(String.valueOf(
                        new java.text.DecimalFormat("##0.#######").format(
                                marker.getPosition().longitude)));
            }

            @Override
            public void onMarkerDrag(Marker marker) {
            }
        });

        //blueharvest.geocaching.util.GeoLocation[] b
        //        = blueharvest.geocaching.util.GeoLocation.fromDegrees(
        //        location.getLatitude(), location.getLongitude())
        //        .boundingCoordinates(distance, 6371.01); // distance & earth's radius in km
        //LatLngBounds here = new LatLngBounds(
        //        new LatLng(b[0].getLatitudeInDegrees(), b[0].getLongitudeInDegrees()),
        //        new LatLng(b[1].getLatitudeInDegrees(), b[1].getLongitudeInDegrees()));
        // Set the camera to the greatest possible zoom level that includes the bounds
        //map.moveCamera(CameraUpdateFactory.newLatLngBounds(here, 0));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here.getCenter(), 10));

        //Circle circle = map.addCircle(new CircleOptions()
        //        .center(here.getCenter())
        //                // http://www.rapidtables.com/web/color/RGB_Color.htm
        //        .fillColor(Color.argb(0x55, 0xd2, 0xb4, 0x8c)) // 85, 210, 180, 140
        //        .strokeColor(Color.argb(0xaa, 0xd2, 0xb4, 0x8c)) // 170, 210, 180, 140
        //        .radius(distance * 1000) // meters (distance is km)
        //        .strokeWidth(3f)); // pixels

        //new GeocachesTask().execute(location);
        //if (geocaches != null) { // null check
        //    for (blueharvest.geocaching.soap.objects.geocache g : geocaches) {
        //        map.addMarker(new MarkerOptions().position(
        //                new LatLng(g.getLocation().getLatitude().getDecimalDegrees(),
        //                        g.getLocation().getLongitude().getDecimalDegrees()))
        //                .title(g.getName()));
        //    }
        //}
    }

    /**
     * Called when the location has changed.
     * <p/>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    /**
     * Called when the provider status changes. This method is called when
     * a provider is unable to fetch a location or if the provider has recently
     * become available after a period of unavailability.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     * @param status   {@link com.blueharvest.geocaching.LocationProvider#OUT_OF_SERVICE} if the
     *                 provider is out of service, and this is not expected to change in the
     *                 near future; {@link com.blueharvest.geocaching.LocationProvider#TEMPORARILY_UNAVAILABLE} if
     *                 the provider is temporarily unavailable but is expected to be available
     *                 shortly; and {@link com.blueharvest.geocaching.LocationProvider#AVAILABLE} if the
     *                 provider is currently available.
     * @param extras   an optional Bundle which will contain provider specific
     *                 status variables.
     *                 <p/>
     *                 <p> A number of common key/value pairs for the extras Bundle are listed
     *                 below. Providers that use any of the keys on this list must
     *                 provide the corresponding value as described below.
     *                 <p/>
     *                 <ul>
     *                 <li> satellites - the number of satellites used to derive the fix
     */
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    public void onProviderEnabled(String provider) {

    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    public void onProviderDisabled(String provider) {

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
         * <p/>
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

    public class AddGeocacheTask extends AsyncTask<blueharvest.geocaching.soap.objects.geocache, Void, Boolean> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Boolean doInBackground(blueharvest.geocaching.soap.objects.geocache... params) {
            Log.d(TAG, params[0].getName());
            try {
                return blueharvest.geocaching.soap.objects.geocache.insert(params[0]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }

    }

}
