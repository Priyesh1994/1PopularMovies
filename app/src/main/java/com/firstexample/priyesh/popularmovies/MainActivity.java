package com.firstexample.priyesh.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.firstexample.priyesh.popularmovies.data.MovieContract;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String first_sort_order;
    private Cursor cur;
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

            Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
            cur = getContentResolver().query(movieUri,null,null,null,null);
            String[] posterArray = new String[cur.getCount()];
            if(cur.moveToFirst())
            {
                int i = 0;
                do{
                    posterArray[i] = cur.getString(cur.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
                    i++;
                }while (cur.moveToNext());
            }

            if(cur.getCount() > 0)
            {
                GridView view = (GridView) findViewById(R.id.movies_grid);
                view.setAdapter(new ImageAdapter(this,posterArray));
                view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        cur.moveToPosition(position);
                        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                        Bundle bundle = new Bundle();

                        //To pass the data to next activity
                        bundle.putString(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
                                cur.getString(cur.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE)));
                        bundle.putString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                                cur.getString(cur.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
                        bundle.putString(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                                cur.getString(cur.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                        bundle.putString(MovieContract.MovieEntry.COLUMN_OVERVIEW,
                                cur.getString(cur.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
                        bundle.putString(MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                                cur.getString(cur.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
            else {
                FetchMovieTask fetchMovieTask = new FetchMovieTask(getApplicationContext(), MainActivity.this);
                fetchMovieTask.execute(first_sort_order);
            }
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
                int rowsDeleted = 0;
                rowsDeleted = getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
                Log.v(LOG_TAG + "Rows Deleted:",rowsDeleted + "");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cur.close();
    }
}
