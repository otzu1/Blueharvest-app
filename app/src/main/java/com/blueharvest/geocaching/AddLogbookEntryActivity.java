package com.blueharvest.geocaching;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddLogbookEntryActivity extends AppCompatActivity {

    // for logging
    public static final String TAG = "blueharvest:: " + AddGeocacheActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_logbook_entry);

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
                    new AddLogbookEntryTask().execute(LogbookEntry(java.util.UUID.fromString(me)));
                    // todo: handle problems with insert
                    /*if (the-insert-failed) {
                        // todo: do something, there's a problem
                    } else {
                        // todo: take the user to the geocache (like below) ... or somewhere else??
                    }*/
                    Intent intent = new Intent(AddLogbookEntryActivity.this, ViewGeocacheActivity.class);
                    intent.putExtra("code", getIntent().getStringExtra("code"));
                    startActivity(intent);
                } else {
                    // todo: something more elegant for the user
                    // todo: they have to log back in and come back
                    System.out.println("me is null. help!");
                }
            }
        });
    }

    private blueharvest.geocaching.soap.objects.logbook.entry LogbookEntry(java.util.UUID userid) {
        return new blueharvest.geocaching.soap.objects.logbook.entry(
                null, null,
                ((TextView) findViewById(R.id.title)).getText().toString(),
                ((TextView) findViewById(R.id.text)).getText().toString(),
                new blueharvest.geocaching.soap.objects.user(
                        userid, null, null, null, null, null,
                        true, false, null, null, null));
    }

    public class AddLogbookEntryTask extends AsyncTask<blueharvest.geocaching.soap.objects.logbook.entry, Void, Boolean> {

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
        protected Boolean doInBackground(blueharvest.geocaching.soap.objects.logbook.entry... params) {
            Log.d(TAG, params[0].getTitle());
            try {
                // important! the logbookid is obtained from the last activity
                // if there is a more elegant way to do this, we should change it
                return blueharvest.geocaching.soap.objects.logbook.entry.insert(
                        params[0], java.util.UUID.fromString(getIntent().getStringExtra("logbookid")));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }
        }

    }

}
