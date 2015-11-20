package com.blueharvest.geocaching;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.GoogleMap;

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
public class AddGeocacheActivity extends AppCompatActivity {

    private GoogleMap map;
    private EditText latitude;
    private EditText longitude;
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
        // take care of the map here.
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
    }


}
