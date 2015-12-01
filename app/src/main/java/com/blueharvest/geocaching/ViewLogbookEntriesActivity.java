package com.blueharvest.geocaching;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewLogbookEntriesActivity extends AppCompatActivity {

    // for logging
    public static final String TAG = "blueharvest:: " + AddGeocacheActivity.class.getSimpleName();

    @Override
    public void onBackPressed() {
        //Log.d(TAG, "onBackPressed");
        Intent intent = new Intent(ViewLogbookEntriesActivity.this, AddLogbookEntryActivity.class);
        //intent.putExtra("logbookid", ((TextView) findViewById(R.id.logbookid)).getText().toString());
        //intent.putExtra("geocacheid", ((TextView) findViewById(R.id.geocacheid)).getText().toString());
        intent.putExtra("code", ((TextView) findViewById(R.id.code)).getText().toString());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_logbook_entries);
        new LogbookEntriesTask().execute(
                java.util.UUID.fromString(getIntent().getStringExtra("logbookid")));
    }

    /**
     * @see <a href="http://developer.android.com/reference/android/os/AsyncTask.html">AsycncTask</a>
     * Params, Progress, Result
     * @since 2015-11-27
     */
    public class LogbookEntriesTask extends AsyncTask<java.util.UUID, Integer, Boolean> {

        private blueharvest.geocaching.soap.objects.logbook l;
        private ProgressDialog pd;

        public LogbookEntriesTask() {
            pd = new ProgressDialog(ViewLogbookEntriesActivity.this);
        }

        @Override
        protected void onPreExecute() {
            pd.setMessage("Fetching logbook entries, please wait.");
            pd.show();
        }

        @Override
        protected Boolean doInBackground(java.util.UUID... params) {
            l = blueharvest.geocaching.soap.objects.logbook.get(params[0]);
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            //super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result && l != null && l.getEntries() != null) {
                // Create the adapter to convert the array to views
                LogbookEntriesAdapter a = new LogbookEntriesAdapter(
                        ViewLogbookEntriesActivity.this, l.getEntries());
                // Attach the adapter to a ListView
                ((ListView) findViewById(R.id.logbookentries)).setAdapter(a);
                //for (blueharvest.geocaching.concepts.logbook.entry e : l.getEntries())
            } else if (!result) { // something went wrong, display a short message
                Toast.makeText(getApplicationContext(),
                        "Logbook feature not available. Please try again later.",
                        Toast.LENGTH_SHORT).show();
            }
            if (pd.isShowing()) pd.dismiss();
        }

    }

    /**
     * @author jmb
     * @see <a href="https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView">
     * Using an ArrayAdapter with ListView</a>
     * <ul><li>Defining the Adapter</li><li>Improving Performance with the ViewHolder Pattern</li></ul>
     * @since 2015-11-27
     */
    public static class LogbookEntriesAdapter
            extends ArrayAdapter<blueharvest.geocaching.concepts.logbook.entry> {

        // View lookup cache
        private static class ViewHolder {
            TextView title;
            TextView datetime;
            TextView text;
        }

        public LogbookEntriesAdapter(
                Context context, ArrayList<blueharvest.geocaching.concepts.logbook.entry> entries) {
            super(context, R.layout.listview_logbook_entry, entries);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            blueharvest.geocaching.concepts.logbook.entry e = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.listview_logbook_entry, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.datetime = (TextView) convertView.findViewById(R.id.datetime);
                viewHolder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            // todo: specify image for the author, caption with username, etc. (ideas)
            // e.getAuthor().getUsername();
            // e.getAuthor().getImage().getUri();
            viewHolder.title.setText(e.getTitle());
            // todo: interval icon, time since, formatting, etc.
            viewHolder.datetime.setText(e.getDate().toString());
            // todo: ellipsize, etc.
            viewHolder.text.setText(e.getText());
            // note: e.getId() is also available for any reason
            // Return the completed view to render on screen
            return convertView;
        }

    }

}
