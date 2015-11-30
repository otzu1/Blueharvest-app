package com.blueharvest.geocaching;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddLogbookEntryActivity extends AppCompatActivity {

    // for logging
    private static final String TAG = "blueharvest:: " + AddGeocacheActivity.class.getSimpleName();

    private java.util.UUID id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_logbook_entry);

        // get user identifier from shared preferences
        final String me = getApplicationContext().getSharedPreferences(
                "blueharvest", MODE_PRIVATE).getString("me", null);
        if (me == null || getIntent().getStringExtra("logbookid").isEmpty()) { // no can do ... let the user know
            Toast.makeText(getApplicationContext(),
                    "Add Logbook Entry is not available. Please try again later.",
                    Toast.LENGTH_SHORT).show();
            findViewById(R.id.save).setEnabled(false);
        } else {
            id = java.util.UUID.fromString(me);
        }

        final EditText title = (EditText) findViewById(R.id.title);
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                title.setError(null);
                if (title.getText().toString().isEmpty())
                    title.setError("Please enter a title.");
            }
        });

        final EditText text = (EditText) findViewById(R.id.text);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                text.setError(null);
                if (text.getText().toString().isEmpty())
                    text.setError("Please enter text.");
            }
        });

        // save button with click listener
        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.getError() != null || title.getText().toString().isEmpty()) {
                    title.setError("Please enter a title.");
                    title.requestFocus();
                    return;
                }
                if (text.getError() != null || text.getText().toString().isEmpty()) {
                    text.setError("Please enter text.");
                    text.requestFocus();
                    return;
                }
                // wrap the controls in a geocache object and
                // send it to the background thread to add the geocache
                new AddLogbookEntryTask().execute();
                Intent intent = new Intent(AddLogbookEntryActivity.this, ViewGeocacheActivity.class);
                intent.putExtra("code", getIntent().getStringExtra("code"));
                startActivity(intent);
            }
        });
    }

    public class AddLogbookEntryTask extends AsyncTask<Void, Void, Boolean> {

        private blueharvest.geocaching.soap.objects.logbook.entry e;

        @Override
        protected void onPreExecute() {
            e = new blueharvest.geocaching.soap.objects.logbook.entry(
                    null, null,
                    ((TextView) findViewById(R.id.title)).getText().toString(),
                    ((TextView) findViewById(R.id.text)).getText().toString(),
                    new blueharvest.geocaching.soap.objects.user(
                            id, null, null, null, null, null,
                            true, false, null, null, null));
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
        protected Boolean doInBackground(Void... params) {
            try {
                // important! the logbookid is obtained from the last activity
                // if there is a more elegant way to do this, we should change it
                return blueharvest.geocaching.soap.objects.logbook.entry.insert(e,
                        java.util.UUID.fromString(getIntent().getStringExtra("logbookid")));
            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) { // something went wrong, let the user know
                Toast.makeText(getApplicationContext(),
                        "The Logbook entry could not be added. Please try again later.",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

}
