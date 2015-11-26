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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geocache);

        // get user identifier from shared preferences
        // if we can't, disable the save button and notify the user
        if (getApplicationContext().getSharedPreferences(
                "blueharvest", MODE_PRIVATE).getString("me", null) == null) {
            Toast.makeText(getApplicationContext(),
                    "Add Geocache is not available. Please try again later.",
                    Toast.LENGTH_SHORT).show();
            findViewById(R.id.save).setEnabled(false);
        }

        // map defaults including latitude and longitude
        setUpMap();

        // non-map defaults
        ((EditText) findViewById(R.id.code)).setText(
                blueharvest.geocaching.soap.objects.geocache.randomCode());

        // type spinner
        // todo: hint for the spinner
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.geocache_types, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        ((Spinner) findViewById(R.id.type)).setAdapter(typeAdapter);

        // size spinner
        // todo: hint for the spinner
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
        // todo: hint for the spinner
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

        // validate user entries
        final EditText latitude = (EditText) findViewById(R.id.latitude);
        latitude.addTextChangedListener(new TextWatcher() {
            /**
             * This method is called to notify you that, within <code>s</code>,
             * the <code>count</code> characters beginning at <code>start</code>
             * are about to be replaced by new text with length <code>after</code>.
             * It is an error to attempt to make changes to <code>s</code> from
             * this callback.
             *
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * This method is called to notify you that, within <code>s</code>,
             * the <code>count</code> characters beginning at <code>start</code>
             * have just replaced old text that had length <code>before</code>.
             * It is an error to attempt to make changes to <code>s</code> from
             * this callback.
             *
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            /**
             * This method is called to notify you that, somewhere within
             * <code>s</code>, the text has been changed.
             * It is legitimate to make further changes to <code>s</code> from
             * this callback, but be careful not to get yourself into an infinite
             * loop, because any changes you make will cause this method to be
             * called again recursively.
             * (You are not told where the change took place because other
             * afterTextChanged() methods may already have made other changes
             * and invalidated the offsets.  But if you need to know here,
             * you can use {@link Spannable#setSpan} in {@link #onTextChanged}
             * to mark your place and then look up from here where the span
             * ended up.
             *
             * Two situations exist where the text would be edited, manually or by
             * dragging the marker.
             *
             * @param s
             */
            @Override
            public void afterTextChanged(Editable s) {
                latitude.setError(null);
                // http://www.regexlib.com/REDetails.aspx?regexp_id=1535
                // ^-?([1-8]?[1-9]|[1-9]0)\.{1}\d{1,7}
                if (!latitude.getText().toString().matches(
                        "^-?([1-8]?[1-9]|[1-9]0)\\.{1}\\d{1,7}"))
                    latitude.setError("Latitude is not in decimal degrees.");
            }
        });

        final EditText longitude = (EditText) findViewById(R.id.longitude);
        longitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                longitude.setError(null);
                // http://www.regexlib.com/REDetails.aspx?regexp_id=1536
                // ^-?([1]?[1-7][1-9]|[1]?[1-8][0]|[1-9]?[0-9])\.{1}\d{1,7}
                if (!longitude.getText().toString().matches(
                        "^-?([1]?[1-7][1-9]|[1]?[1-8][0]|[1-9]?[0-9])\\.{1}\\d{1,7}"))
                    longitude.setError("Longitude must be in decimal degrees.");
            }
        });

        final EditText name = (EditText) findViewById(R.id.name);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                name.setError(null);
                if (name.getText().toString().isEmpty())
                    name.setError("Name cannot be empty.");
            }
        });

        final EditText description = (EditText) findViewById(R.id.description);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                description.setError(null);
                if (description.getText().toString().isEmpty())
                    description.setError("Description cannot be empty.");
            }
        });

        final EditText code = (EditText) findViewById(R.id.code);
        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                code.setError(null);
                if (code.getText().toString().isEmpty()) {
                    code.setError("Code cannot be empty.");
                }
            }
        });

        // save button with click listener
        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // validate
                if (latitude.getError() != null) {
                    latitude.requestFocus();
                    return;
                }
                if (longitude.getError() != null) {
                    longitude.requestFocus();
                    return;
                }
                if (name.getError() != null || name.getText().toString().isEmpty()) {
                    name.requestFocus();
                    return;
                }
                if (description.getError() != null || description.getText().toString().isEmpty()) {
                    description.requestFocus();
                    return;
                }
                if (code.getError() != null || description.getText().toString().isEmpty()) {
                    code.setText(blueharvest.geocaching.soap.objects.geocache.randomCode());
                    code.requestFocus();
                    return;
                }
                // good to go!
                // wrap the widget texts in a geocache object and
                // send it to the background thread to add the geocache
                // error handling is taken care of in the task
                new AddGeocacheTask().execute(Geocache(java.util.UUID.fromString(
                        getApplicationContext().getSharedPreferences(
                                "blueharvest", MODE_PRIVATE).getString("me", null))));
                Intent intent = new Intent(AddGeocacheActivity.this, ViewGeocacheActivity.class);
                intent.putExtra("code", ((EditText) findViewById(R.id.code)).getText().toString());
                startActivity(intent);
            }
        });
        /*Dialog dialog = GooglePlayServicesUtil.getErrorDialog(0, this, 0);
        dialog.show();*/
    }

    /**
     * transforms the controls into a geocache object
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
                        -1, null), // altitude
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
                } else { // set up a mock location
                    handleLocation(40.7981884, -77.8599151); // Penn State
                }
                // 10 minutes should be plenty ... if not, increase this
                locationManager.requestLocationUpdates(provider, 600000, 0, this);
            }
        }
    }

    /**
     * do this preferably once so that the user can edit the values without interference
     * @param latitude
     * @param longitude
     */
    private void handleLocation(double latitude, double longitude) {
        // clear the map
        map.clear();
        // map type
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        // Showing the current location in Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
        // Zoom in the Google Map
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        // Geocache Latitude and Longitude
        if (((EditText) findViewById(R.id.latitude)).getText().toString().isEmpty()
                && ((EditText) findViewById(R.id.longitude)).getText().toString().isEmpty()) {
            ((EditText) findViewById(R.id.latitude)).setText(String.valueOf(
                    new java.text.DecimalFormat("##0.#######").format(latitude)));
            ((EditText) findViewById(R.id.longitude)).setText(String.valueOf(
                    new java.text.DecimalFormat("##0.#######").format(longitude)));
        }
        // location marker
        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
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

        // surrounding geocaches
        new GeocachesTask().execute(latitude, longitude, 10d);
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
        handleLocation(location.getLatitude(), location.getLongitude());
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
    public class GeocachesTask extends AsyncTask<Double, Integer, Void> {

        private blueharvest.geocaching.soap.objects.geocache.geocaches geocaches;

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         *               params[0] is latitude, params[1] is longitude, and params[2] is distance
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Double... params) {
            geocaches = new blueharvest.geocaching.soap.objects.geocache.geocaches(
                    params[0], params[1], params[2]);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        /**
         * set up the geocache markers
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Void result) {
            if (geocaches != null) { // null check
                for (blueharvest.geocaching.soap.objects.geocache g : geocaches) {
                    map.addMarker(new MarkerOptions().position(
                            new LatLng(g.getLocation().getLatitude().getDecimalDegrees(),
                                    g.getLocation().getLongitude().getDecimalDegrees()))
                            .title(g.getName())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                }
            } else if (geocaches == null) { // the nearby geocaches failed
                Toast.makeText(getApplicationContext(),
                        "Nearby geocaches is not available.",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class AddGeocacheTask
            extends AsyncTask<blueharvest.geocaching.soap.objects.geocache, Void, Boolean> {

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
            //Log.d(TAG, params[0].getName());
            try {
                return blueharvest.geocaching.soap.objects.geocache.insert(params[0]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result == null) { // an exception occurred in the web service call
                Toast.makeText(getApplicationContext(),
                        "Add Geocache is not available. Please try again later.",
                        Toast.LENGTH_SHORT).show();
            } else if (!result) { // the web service returned false, the insert failed
                Toast.makeText(getApplicationContext(),
                        "The Geocache could not be added. Please try again later.",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

}
