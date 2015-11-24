package com.blueharvest.geocaching;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class user_home_page extends AppCompatActivity implements LocationListener {

    private GoogleMap mMap;
    private final static int MY_LOCATION_PERMISSION = 1;
    private double distance = 10; // km

    private SearchTask mSearchTask = null;

    private static final String logCat = "user home page";
    blueharvest.geocaching.soap.objects.geocache.geocaches results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_user_home_page);

        Log.d("blueharvest", "user_home_page.java");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Double searchRadius = getIntent().getDoubleExtra("SearchRad", 0.00);
        Double searchLat = getIntent().getDoubleExtra("SearchLat", 0.00);
        Double searchLon = getIntent().getDoubleExtra("SearchLon", 0.00);

        View mMapView = findViewById(R.id.user_home_content_form);

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

        Location center = new Location ("New");
        center.setLatitude(searchLat);
        center.setLongitude(searchLon);
        //mSearchTask = new SearchTask(center);
        new SearchTask().execute(center);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_LOCATION_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    setupMap();

                } else {


                }
            }
        }

    }

    public void setupMap() {
        Log.d(logCat, "setupMap");
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class SearchTask extends AsyncTask<Location, Void, Void> {

        // jmb: added private object for onpostexecute 2015-11-24
        private blueharvest.geocaching.soap.objects.geocache.geocaches gs;

        /*public SearchTask(Location center) {

        }*/

        @Override
        protected Void doInBackground(Location... locations) {
            for (int i = 0; i < locations.length; i++) { // should only be one
                double myLatitude = locations[i].getLatitude();
                double myLongitude = locations[i].getLongitude();
                // get the geocaches from around the location coordinates here
                gs = new blueharvest.geocaching.soap.objects.geocache.geocaches(
                        myLatitude, myLongitude, distance);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Generate the markers on the map
            for(blueharvest.geocaching.soap.objects.geocache geocache : gs) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(geocache.getLocation().getLatitude().getDecimalDegrees(),
                                geocache.getLocation().getLongitude().getDecimalDegrees()))
                        .title(geocache.getName())
                        .snippet(geocache.getCode()));
            }
        }

        @Override
        protected void onCancelled() {
            mSearchTask = null;
        }
    }


}

