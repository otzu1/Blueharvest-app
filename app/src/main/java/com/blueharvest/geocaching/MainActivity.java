package com.blueharvest.geocaching;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * @see <a href="http://blog.teamtreehouse.com/add-navigation-drawer-android">
 * How to Add a Navigation Drawer in Android</a>
 * @see <a href="https://github.com/treehouse/android-navigation-drawer-final/blob/master/app/src/main/java/com/teamtreehouse/oslist/MainActivity.java">
 * android-navigation-drawer-final/app/src/main/java/com/teamtreehouse/oslist/MainActivity.java</a>
 * android.support.v7.app.ActionBarActivity is deprecated ...
 * using android.support.v7.app.AppCompatActivity instead
 */
public class MainActivity extends AppCompatActivity implements LocationListener {

    // for logging
    public static final String TAG = "blueharvest:: " + MainActivity.class.getSimpleName();

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;

    private GoogleMap map;

    private double distance = 10; // km

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // map
        setUpMap();

    }

    /**
     * todo: personalize
     */
    private void addDrawerItems() {
        String[] osArray = {"Android", "iOS", "Windows", "OS X", "Linux"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Callback method to be invoked when an item in this AdapterView has
             * been clicked.
             * <p/>
             * Implementers can call getItemAtPosition(position) if they need
             * to access the data associated with the selected item.
             *
             * @param parent   The AdapterView where the click happened.
             * @param view     The view within the AdapterView that was clicked (this
             *                 will be a view provided by the adapter)
             * @param position The position of the view in the adapter.
             * @param id       The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * todo: personalize
     */
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // todo: fixme
                //getActionBar().setTitle("Navigation!");
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                // todo: fixme
                //getActionBar().setTitle(mActivityTitle);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * copied from addgeocacheactivity
     */
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

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the location has changed.
     * <p>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, location.toString());
        // Showing the current location in Google Map
        map.moveCamera(CameraUpdateFactory.newLatLng(
                new LatLng(location.getLatitude(), location.getLongitude())));
        // Zoom in the Google Map
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        // todo: Latitude and Longitude
        /*((EditText) findViewById(R.id.latitude)).setText(String.valueOf(
                new java.text.DecimalFormat("##0.#######").format(location.getLatitude())));
        ((EditText) findViewById(R.id.longitude)).setText(String.valueOf(
                new java.text.DecimalFormat("##0.#######").format(location.getLongitude())));*/
        // Current location marker
        // todo: uncomment to mark current location
        /*map.addMarker(new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                //.draggable(true)
                .title("Here I am!"));*/
        /*map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
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
        });*/

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

        new GeocachesTask().execute(location);
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
     *                 <p>
     *                 <p> A number of common key/value pairs for the extras Bundle are listed
     *                 below. Providers that use any of the keys on this list must
     *                 provide the corresponding value as described below.
     *                 <p>
     *                 <ul>
     *                 <li> satellites - the number of satellites used to derive the fix
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
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
    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * @see <a href="http://developer.android.com/reference/android/os/AsyncTask.html">AsycncTask</a>
     * Params, Progress, Result
     * @since 2015-11-20
     */
    public class GeocachesTask extends AsyncTask<Location, Integer, Void> {

        private blueharvest.geocaching.soap.objects.geocache.geocaches g;

        protected Void doInBackground(Location... locations) {
            for (int i = 0; i < locations.length; i++) { // should only be one
                double myLatitude = locations[i].getLatitude();
                double myLongitude = locations[i].getLongitude();
                // get the geocaches from around the location coordinates here
                g = new blueharvest.geocaching.soap.objects.geocache.geocaches(
                        myLatitude, myLongitude, distance);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            if (g != null) { // null check
                for (blueharvest.geocaching.soap.objects.geocache x : g) {
                    map.addMarker(new MarkerOptions().position(
                            new LatLng(x.getLocation().getLatitude().getDecimalDegrees(),
                                    x.getLocation().getLongitude().getDecimalDegrees()))
                            .title(x.getName()));
                }
            }
        }

    }

}
