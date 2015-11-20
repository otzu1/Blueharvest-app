package com.blueharvest.geocaching;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class AddGeocacheActivity extends AppCompatActivity {

    private EditText latitude;
    private EditText longitude;
    private EditText name;
    private EditText description;
    private EditText code;
    /*private ListView size;
    private ListView terrain;
    private ListView difficulty;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_geocache);

        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);
        code = (EditText) findViewById(R.id.code);
        /*size = (ListView) findViewById(R.id.size);
        final String[] sizes = new String[]{"super petit", "petit", "moyen",
                "grand", "extra large", "grandiose", "enorme", "inconnu", "aucun"};
        final ArrayAdapter<String> sizeadapter
                = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, sizes);
        size.setAdapter(sizeadapter);*/

        //mUsernameView = (EditText) findViewById(R.id.createUserName


    }

}
