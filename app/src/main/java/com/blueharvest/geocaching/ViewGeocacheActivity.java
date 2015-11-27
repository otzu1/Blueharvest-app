package com.blueharvest.geocaching;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.URL;

import blueharvest.geocaching.concepts.location;

/**
 * View Geocache Activity<br>
 * View of a geocache with a map to of the latitude and longitude coordinates. The geocache
 * attributes are all shown on this activity. The "code" message MUST be sent from the
 * preceding activity to function properly ... without it, the activity will fail.
 * The favorite control is shown with a filled star when the user has "favorited" the geocache
 * and unfilled when the geocache is not a favorite of the user. The display is similar when the
 * user has indicated the geocache was already found or not depending in whether the "found"
 * circle is filled or not. The avatar (if available) is displayed for the user responsible
 * for creating the geocache and the anniversary date in which the geocache was created.
 * The user has the option to log the geocache from this activity from a stationary "log" button.
 * The layout above the log button scrolls with room to spare at the bottom to avoid blocking the
 * geocache information.
 * <p/>
 * todo: send intent message to a log activity
 * todo: send intent to view all logs of this geocache
 * todo: get current location to display the distance to the geocache (requires LocationListener)
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

        // get the geocache and set up the view in the onpostexecute function
        try {
            //new GeocacheTask().execute(getIntent().getStringExtra("code"));
            new GeocacheTask().execute("BH13GC7"); // todo: testing
        } catch (Exception e) { // something went wrong, display a short message
            Toast.makeText(getApplicationContext(),
                    "Geocache could not be fetched. Please try again later!",
                    Toast.LENGTH_SHORT).show();
        }

        // favorite custom checkbox
        final CheckBox favorite = (CheckBox) findViewById(R.id.favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DoFavoriteTask().execute(
                        getApplicationContext().getSharedPreferences(
                                "blueharvest", MODE_PRIVATE).getString("me", null),
                        ((TextView) findViewById(R.id.geocacheid)).getText().toString(),
                        String.valueOf(favorite.isChecked()));
            }
        });

        // found custom checkbox
        final CheckBox found = (CheckBox) findViewById(R.id.found);
        found.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DoFoundTask().execute(
                        getApplicationContext().getSharedPreferences(
                                "blueharvest", MODE_PRIVATE).getString("me", null),
                        ((TextView) findViewById(R.id.geocacheid)).getText().toString(),
                        String.valueOf(found.isChecked()));
            }
        });

        // log button with click listener
        Button log = (Button) findViewById(R.id.log);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewGeocacheActivity.this, AddLogbookEntryActivity.class);
                intent.putExtra("logbookid", ((TextView) findViewById(R.id.logbookid)).getText().toString());
                intent.putExtra("geocacheid", ((TextView) findViewById(R.id.geocacheid)).getText().toString());
                intent.putExtra("code", ((TextView) findViewById(R.id.code)).getText().toString());
                startActivity(intent);
            }
        });
    }

    @Override
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
     *
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

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
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

        @Override
        protected Void doInBackground(String... params) {
            try {
                g = blueharvest.geocaching.soap.objects.geocache.get(params[0]);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Sets up the map and widgets on this Activity from the
         * geocache object fetched from doInBackground
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Void result) {
            if (g != null) {
                map.moveCamera(CameraUpdateFactory.newLatLng(
                        new LatLng(g.getLocation().getLatitude().getDecimalDegrees(),
                                g.getLocation().getLongitude().getDecimalDegrees())));
                // Zoom in the Google Map
                map.animateCamera(CameraUpdateFactory.zoomTo(15));
                // Geocache location marker with name
                map.addMarker(new MarkerOptions()
                        .position(new LatLng(g.getLocation().getLatitude().getDecimalDegrees(),
                                g.getLocation().getLongitude().getDecimalDegrees()))
                        .title(g.getName()));
                // set up widgets

                ((TextView) findViewById(R.id.latitude)).setText(
                        g.getLocation().getLatitude().toSexigesimal(
                                location.coordinate.type.latitude));
                ((TextView) findViewById(R.id.longitude)).setText(
                        g.getLocation().getLongitude().toSexigesimal(
                                location.coordinate.type.longitude));
                // userid for custom checkboxes
                final String me = getApplicationContext().getSharedPreferences(
                        "blueharvest", MODE_PRIVATE).getString("me", null);
                // favorite default value
                new IsFavoriteTask().execute(g.getId(), java.util.UUID.fromString(me));
                // found default value
                new IsFoundTask().execute(g.getId(), java.util.UUID.fromString(me));
                ((TextView) findViewById(R.id.name)).setText(g.getName());
                ((TextView) findViewById(R.id.code)).setText(g.getCode());
                ((TextView) findViewById(R.id.type)).setText(
                        getResources().getStringArray(R.array.geocache_types)[g.getType()]);
                ((TextView) findViewById(R.id.size)).setText(
                        getResources().getStringArray(R.array.geocache_sizes)[g.getSize()]);
                ((TextView) findViewById(R.id.terrain)).setText(
                        getResources().getStringArray(R.array.geocache_terrain)[g.getTerrain()]);
                ((TextView) findViewById(R.id.difficulty)).setText(
                        getResources().getStringArray(R.array.geocache_difficulty)[g.getDifficulty()]);
                // todo: fade out long descriptions and offer a button to "read more"
                ((TextView) findViewById(R.id.description)).setText(g.getDescription());
                // todo: creator image (avatar)
                if (g.getCreator().getImage() != null && g.getCreator().getImage().getUri() != null) {
                    /* Download the image from online and set it as ImageView image programmatically. */
                    new DownloadUserImageTask(((ImageView) findViewById(R.id.creator_image))).execute(
                            g.getCreator().getImage().getUri().toString());
                }
                ((TextView) findViewById(R.id.creator)).setText("Placed by: " + g.getCreator().getUsername());
                // todo: format anniversary
                ((TextView) findViewById(R.id.anniversary)).setText(g.getAnniversary().toString());
                ((TextView) findViewById(R.id.geocacheid)).setText(g.getId().toString());
                ((TextView) findViewById(R.id.logbookid)).setText(g.getLogbook().getId().toString());
            } else { // something went wrong, display a short message and disable/hide log button
                Toast.makeText(getApplicationContext(),
                        "Geocache could not be fetched. Please try again later.",
                        Toast.LENGTH_SHORT).show();
                findViewById(R.id.log).setEnabled(false);
                findViewById(R.id.log).setVisibility(View.INVISIBLE);
            }
        }

    }

    /**
     * @see <a href="http://developer.android.com/reference/android/os/AsyncTask.html">AsycncTask</a>
     * Params, Progress, Result
     * @since 2015-11-24
     */
    public class DoFavoriteTask extends AsyncTask<String, Void, Boolean> {

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
        protected Boolean doInBackground(String... params) {
            try {
                // params[0] = userid, params[1] = geocacheid, params[2] = true/false
                return blueharvest.geocaching.soap.objects.user.relateFavoriteGeocache(
                        java.util.UUID.fromString(params[0]), java.util.UUID.fromString(params[1]),
                        Boolean.parseBoolean(params[2]));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * handle any problems
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) { // something went wrong, display a short message
                Toast.makeText(getApplicationContext(),
                        "Geocache Favorite feature not available. Please try again later.",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * @see <a href="http://developer.android.com/reference/android/os/AsyncTask.html">AsycncTask</a>
     * Params, Progress, Result
     * @since 2015-11-24
     */
    public class IsFavoriteTask extends AsyncTask<java.util.UUID, Void, Boolean> {

        private boolean b;

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
        protected Boolean doInBackground(java.util.UUID... params) {
            try {
                // params[0] = geocacheid and params[1] = userid
                b = blueharvest.geocaching.soap.objects.geocache.isFavorite(params[0], params[1]);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * handle any problems
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) { // something went wrong, display a short message
                Toast.makeText(getApplicationContext(),
                        "Geocache Favorite feature (default) not available. Please try again later.",
                        Toast.LENGTH_SHORT).show();
            } else if (result) {
                ((CheckBox) findViewById(R.id.favorite)).setChecked(b);
            }
        }

    }

    /**
     * @see <a href="http://developer.android.com/reference/android/os/AsyncTask.html">AsycncTask</a>
     * Params, Progress, Result
     * @since 2015-11-24
     */
    public class DoFoundTask extends AsyncTask<String, Void, Boolean> {

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
        protected Boolean doInBackground(String... params) {
            try {
                // params[0] = userid, params[2] = geocacheid, params[2] = true/false
                return blueharvest.geocaching.soap.objects.user.relateFoundGeocache(
                        java.util.UUID.fromString(params[0]),
                        java.util.UUID.fromString(params[1]),
                        Boolean.valueOf(params[2]));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * handle any problems here
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) { // something went wrong, display a short message
                Toast.makeText(getApplicationContext(),
                        "Geocache Found feature not available. Please try again later.",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * @see <a href="http://developer.android.com/reference/android/os/AsyncTask.html">AsycncTask</a>
     * Params, Progress, Result
     * @since 2015-11-24
     */
    public class IsFoundTask extends AsyncTask<java.util.UUID, Void, Boolean> {

        private boolean b;

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
        protected Boolean doInBackground(java.util.UUID... params) {
            try {
                // params[0] = geocacheid and params[1] = userid
                b = blueharvest.geocaching.soap.objects.geocache.isFound(params[0], params[1]);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * handle any problems
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) { // something went wrong, display a short message
                Toast.makeText(getApplicationContext(),
                        "Geocache Favorite feature (default) not available. Please try again later.",
                        Toast.LENGTH_SHORT).show();
            } else if (result) {
                ((CheckBox) findViewById(R.id.found)).setChecked(b);
            }
        }

    }

    /**
     * todo: this code is here for when we implement user images
     *
     * @see <a href="http://android--code.blogspot.com/2015/08/android-imageview-set-image-from-url.html">
     * How to set ImageView image from URL in Android</a>
     * @since 2015-11-23
     */
    private class DownloadUserImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView;

        public DownloadUserImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

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
        protected Bitmap doInBackground(String... params) {
            Bitmap r = null;
            try {
                InputStream is = new URL(params[0]).openStream();
                /* decode an input stream into a bitmap */
                r = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return r;
        }

        /**
         * onPostExecute(Result result)
         * Runs on the UI thread after doInBackground(Params...)
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

    }

}
