package com.blueharvest.geocaching;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class user_page extends AppCompatActivity implements LocationListener {

    private GoogleMap mMap;
    private EditText mLatitude;
    private EditText mLongitude;
    private EditText mSearchRad;
    private final static int MY_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        Log.d("blueharvest", "user_page.java");

        mLatitude = (EditText) findViewById(R.id.latitude);
        mLongitude = (EditText) findViewById(R.id.longitude);
        mSearchRad = (EditText) findViewById(R.id.searchRad);

        View mMapView = findViewById(R.id.mapParent);

        Button mSearch = (Button) findViewById(R.id.buttonSearch);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("blueharvest", "search button clicked");
                // this is never reached as far as I can see in the log
                searchGeocache();
            }
        });

        Button mAddGeocache = (Button) findViewById(R.id.buttonAddGeo);
        mAddGeocache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("blueharvest", "add geocache button clicked");
                addGeocache();
            }
        });

        Button mListSearch = (Button) findViewById(R.id.buttonListSearch);
        mListSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("blueharvest", "search list button clicked");
                // this is never reached as far as I can see in the log
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
                        @Override
                        public void onClick(View view) {
                            // Request the permission
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
                }
                locationManager.requestLocationUpdates(provider, 20000, 0, this);
            }
        }
    }

    protected void listSearchGeocache() {
        Log.d("blueharvest.user_page", "listSearchGeocache called");
        Intent listSearchIntent = new Intent(user_page.this, ViewGeocachesActivity.class);
        Double searchRad = Double.parseDouble(mSearchRad.getText().toString());
        Double searchLat = Double.parseDouble(mLatitude.getText().toString());
        Double searchLong = Double.parseDouble(mLongitude.getText().toString());
        listSearchIntent.putExtra("SearchRad", searchRad);
        listSearchIntent.putExtra("SearchLat", searchLat);
        listSearchIntent.putExtra("SearchLong", searchLong);
        startActivity(listSearchIntent);
        finish();

    }
    protected void searchGeocache() {
        Log.d("blueharvest.user_page", "searchGeocache called");
        Intent searchIntent = new Intent(user_page.this, user_home_page.class);
        Double searchRad = Double.parseDouble(mSearchRad.getText().toString());
        Double searchLat = Double.parseDouble(mLatitude.getText().toString());
        Double searchLong = Double.parseDouble(mLongitude.getText().toString());
        searchIntent.putExtra("SearchRad", searchRad);
        searchIntent.putExtra("SearchLat", searchLat);
        searchIntent.putExtra("SearchLong", searchLong);
        startActivity(searchIntent);
        finish();
    }

    protected void addGeocache() {
        Log.d("blueharvest.user_page", "addGeocache called");
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
