package com.firstexample.priyesh.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firstexample.priyesh.popularmovies.data.MovieContract;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String first_sort_order;
    //private final Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        first_sort_order = prefs.getString(getString(R.string.sort_key),
                getString(R.string.pref_sort_popular));
        //MainMovieFragment movieFragment = new MainMovieFragment();
    //        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_main_movie,movieFragment)
//                .commit();
        /*MainMovieFragment movieFragment = (MainMovieFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_main_movie);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.weather_detail_container, fragment, DETAILFRAGMENT_TAG).commit();*/
        //updateMovies();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainMovieFragment mainMovieFragment = (MainMovieFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main_movie);
        if(mainMovieFragment.isOnline()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            //will check whether the preference has changed or not
            if (!first_sort_order.equals(prefs.getString(getString(R.string.sort_key),
                    getString(R.string.pref_sort_popular)))) {
                if(!first_sort_order.equals(getString(R.string.pref_sort_favourites))) {
                    int rowsDeleted = 0;
                    rowsDeleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
                    Log.v(LOG_TAG + "Rows Deleted:", rowsDeleted + "");
                }
                mainMovieFragment.updateMovies();
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

/*    @Override
    protected void onDestroy() {
        super.onDestroy();
        cur.close();
    }*/
}
