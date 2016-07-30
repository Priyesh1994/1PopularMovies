package com.firstexample.priyesh.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String first_sort_order;
    //private final Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateMovies();
    }


    //To check for internet connection
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void updateMovies() {
        if(isOnline()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            first_sort_order = prefs.getString(getString(R.string.sort_key),
                    getString(R.string.pref_sort_popular));

            FetchMovieTask fetchMovieTask = new FetchMovieTask(getApplicationContext(),MainActivity.this);
            fetchMovieTask.execute(first_sort_order);
        }
        else
        {
            Toast.makeText(this,"Network Issue",Toast.LENGTH_SHORT).show();
            Log.v(LOG_TAG,"Network error");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(isOnline()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            //will check whether the preference has changed or not
            if (!first_sort_order.equals(prefs.getString(getString(R.string.sort_key),
                    getString(R.string.pref_sort_popular)))) {
                updateMovies();
            }
        }
        else
        {
            Toast.makeText(this,"Network Issue",Toast.LENGTH_SHORT).show();
            Log.v(LOG_TAG,"Network error");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainactivity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.settings)
        {
            startActivity(new Intent(this,Settings.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
