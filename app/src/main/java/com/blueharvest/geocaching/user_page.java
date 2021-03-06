package com.blueharvest.geocaching;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * This activity is like a dashboard for the user and offers a map to view current
 * location, entry for latitude and longitude, search based on latitude, longitude, and
 * radius later displayed in either a map view or a list view of the results. Additionally,
 * this activity offers the ability to add a geocache, go to help or settings, and to log out.
 * <p/>
 * todo: marker for latitude and longitude entry (idea)
 * todo: move map after latitude and longitude entry (idea)
 * todo: consider a draggable marker for latitude and longitude (like add geocache activity)
 * todo: validation before any search button (map or list)
 * todo: logout button
 */
public class user_page extends AppCompatActivity implements LocationListener {

    // for logging
    public static final String TAG = "blueharvest:: " + AddGeocacheActivity.class.getSimpleName();

    private GoogleMap mMap;
    //private EditText mLatitude;
    //private EditText mLongitude;
    //private EditText mSearchRad;
    private final static int MY_LOCATION_PERMISSION = 1;

    private int obp = 0;

    public void onBackPressed() {
        //Log.d(TAG, "onBackPressed");
        obp++;
        if (obp == 1) {
            Toast.makeText(user_page.this, "Please press back again to exit.",
                    Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        //Log.d(TAG, "user_page.java");

        //mLatitude = (EditText) findViewById(R.id.latitude);
        //mLongitude = (EditText) findViewById(R.id.longitude);
        //mSearchRad = (EditText) findViewById(R.id.searchRad);

        View mMapView = findViewById(R.id.mapParent);

        // validate user entries
        final EditText latitude = (EditText) findViewById(R.id.latitude);
        latitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                latitude.setError(null);
                if (latitude.getText().toString().isEmpty()
                        || (latitude.getText().toString().length() > 1
                        && (Double.parseDouble(latitude.getText().toString()) < -90
                        || Double.parseDouble(latitude.getText().toString()) > 90)))
                    latitude.setError("Please enter latitude in decimal degrees.");
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
                if (longitude.getText().toString().isEmpty()
                        || (longitude.getText().toString().length() > 1
                        && (Double.parseDouble(longitude.getText().toString()) > 180
                        || Double.parseDouble(longitude.getText().toString()) < -180)))
                    longitude.setError("Please enter longitude in decimal degrees.");
            }
        });

        final EditText radius = (EditText) findViewById(R.id.searchRad);
        radius.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                radius.setError(null);
                if (radius.getText().toString().isEmpty()
                        || Double.parseDouble(radius.getText().toString()) < 0 // negative values
                        || 6371 < Double.parseDouble(radius.getText().toString())) { // > earth's radius
                    radius.setError("Please enter a valid radius between 0 and 6371.");
                }

            }
        });

        Button mSearch = (Button) findViewById(R.id.buttonSearch);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "search button clicked");
                // validate user input
                if (latitude.getError() != null || latitude.getText().toString().isEmpty()) {
                    latitude.setError("Please enter latitude in decimal degrees.");
                    latitude.requestFocus();
                    return;
                }
                if (longitude.getError() != null || longitude.getText().toString().isEmpty()) {
                    longitude.setError("Please enter latitude in decimal degrees.");
                    longitude.requestFocus();
                    return;
                }
                if (radius.getError() != null || radius.getText().toString().isEmpty()) {
                    radius.setError("Please enter a valid radius between 0 and 6371.");
                    radius.requestFocus();
                    return;
                }
                searchGeocache();
            }
        });

        Button mAddGeocache = (Button) findViewById(R.id.buttonAddGeo);
        mAddGeocache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "add geocache button clicked");
                addGeocache();
            }
        });

        Button mListSearch = (Button) findViewById(R.id.buttonListSearch);
        mListSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "search list button clicked");
                // validate user input
                if (latitude.getError() != null) {
                    latitude.setError("Please enter latitude in decimal degrees.");
                    latitude.requestFocus();
                    return;
                }
                if (longitude.getError() != null) {
                    longitude.setError("Please enter latitude in decimal degrees.");
                    longitude.requestFocus();
                    return;
                }
                if (radius.getError() != null || radius.getText().toString().isEmpty()) {
                    radius.setError("Please enter a valid radius between 0 and 6371.");
                    radius.requestFocus();
                    return;
                }
                listSearchGeocache();
            }
        });

        Button mSettings = (Button) findViewById(R.id.buttonSettings);
        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettings();
            }
        });

        Button mHelp = (Button) findViewById(R.id.buttonHelp);
        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openHelp();
            }
        });

        /* Check for permissions to access the fine location.  If permissions not
           granted, give and explanation and request the permissions.
         */
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            setupMap();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Snackbar.make(mMapView, "Location access is required to show your current position.",
                            Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(View view) {
                            // Request the permission
                            // todo: this causes error (api min 23)
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_LOCATION_PERMISSION);
                        }
                    }).show();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_LOCATION_PERMISSION);
                }
            }
        }
    }

    public void setupMap() {
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
            mMap = fm.getMap();

            // Enabling MyLocation Layer of Google Map
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                // Getting Current Location
                Location location = locationManager.getLastKnownLocation(provider);

                if (location != null) {
                    onLocationChanged(location);
                } else { // jmb: introduce a mock location
                    Location l = new Location("");
                    // Penn State
                    l.setLatitude(40.7981884);
                    l.setLongitude(-77.8599151);
                    onLocationChanged(l); // reuse onLocationChanged for now ...
                }
                // 10 minutes should be more than enough ... if not, change this
                locationManager.requestLocationUpdates(provider, 600000, 0, this);
            }
        }
    }

    protected void listSearchGeocache() {
        //Log.d(TAG, "listSearchGeocache called");
        Intent listSearchIntent = new Intent(user_page.this, ViewGeocachesActivity.class);
        Double searchRad = Double.parseDouble(((EditText) findViewById(R.id.searchRad)).getText().toString());
        Double searchLat = Double.parseDouble(((EditText) findViewById(R.id.latitude)).getText().toString());
        Double searchLong = Double.parseDouble(((EditText) findViewById(R.id.longitude)).getText().toString());
        listSearchIntent.putExtra("SearchRad", searchRad);
        listSearchIntent.putExtra("SearchLat", searchLat);
        listSearchIntent.putExtra("SearchLong", searchLong);
        startActivity(listSearchIntent);
        finish();
    }

    protected void searchGeocache() {
        //Log.d(TAG, "searchGeocache called");
        Intent searchIntent = new Intent(user_page.this, user_home_page.class);
        Double searchRad = Double.parseDouble(((EditText) findViewById(R.id.searchRad)).getText().toString());
        Double searchLat = Double.parseDouble(((EditText) findViewById(R.id.latitude)).getText().toString());
        Double searchLong = Double.parseDouble(((EditText) findViewById(R.id.longitude)).getText().toString());
        searchIntent.putExtra("SearchRad", searchRad);
        searchIntent.putExtra("SearchLat", searchLat);
        searchIntent.putExtra("SearchLong", searchLong);
        startActivity(searchIntent);
        finish();
    }

    protected void addGeocache() {
        //Log.d(TAG, "addGeocache called");
        finish();
        startActivity(new Intent(user_page.this, AddGeocacheActivity.class));
    }

    protected void openSettings() {
        finish();
        startActivity(new Intent(user_page.this, SettingsActivity.class));
    }

    protected void openHelp() {
        finish();
        startActivity(new Intent(user_page.this, HelpActivity.class));
    }

    @Override
    public void onLocationChanged(Location location) {

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // Update the lat and longitude fields
        /*String lat = Double.valueOf(latitude).toString();
        String lon = Double.valueOf(longitude).toString();
        mLatitude.setText(lat, TextView.BufferType.NORMAL);
        mLongitude.setText(lon, TextView.BufferType.NORMAL);*/

        // using format to restrict the decimal places
        ((EditText) findViewById(R.id.latitude)).setText(String.valueOf(
                new java.text.DecimalFormat("##0.#######").format(latitude)));
        ((EditText) findViewById(R.id.longitude)).setText(String.valueOf(
                new java.text.DecimalFormat("##0.#######").format(longitude)));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

}
