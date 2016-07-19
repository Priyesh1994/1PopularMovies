package com.firstexample.priyesh.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String overview,release_date,vote_average,original_title,moviePoster;
    private String first_sort_order;

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

            FetchMovieTask fetchMovieTask = new FetchMovieTask();
            fetchMovieTask.execute(first_sort_order);
        }
        else
        {
            Toast.makeText(this,"Network Issue",Toast.LENGTH_SHORT).show();
            Log.v(LOG_TAG,"Network error");
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //will check whether the preference has changed or not
        if(!first_sort_order.equals(prefs.getString(getString(R.string.sort_key),
                getString(R.string.pref_sort_popular)))) {
            updateMovies();
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

    public class FetchMovieTask extends AsyncTask<String,Void,String>{

        final String POSTER_PATH = "poster_path";
        final String RELEASE_DATE = "release_date";
        final String OVERVIEW = "overview";
        final String ORIGINAL_TITLE = "original_title";
        final String VOTE_AVERAGE = "vote_average";
        final String RESULT_STRING = "results";
        final String BASE_URL = "http://image.tmdb.org/t/p/w185/";

        @Override
        protected void onPostExecute(final String movieString) {
            GridView view = (GridView) findViewById(R.id.movies_grid);
            String[] posterArray = new String[0];
            try {
                posterArray = getDataFromJSON(movieString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            view.setAdapter(new ImageAdapter(MainActivity.this,posterArray));

            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                    Bundle bundle = new Bundle();

                    JSONObject posterJSON = null;
                    try {
                        posterJSON = new JSONObject(movieString);
                        JSONArray movieArray = posterJSON.getJSONArray(RESULT_STRING);
                        JSONObject movieDetails = movieArray.getJSONObject(position);
                        //To retrieve details of the selected movie
                        moviePoster = movieDetails.getString(POSTER_PATH);
                        release_date = movieDetails.getString(RELEASE_DATE);
                        overview = movieDetails.getString(OVERVIEW);
                        original_title = movieDetails.getString(ORIGINAL_TITLE);
                        vote_average = movieDetails.getString(VOTE_AVERAGE);
                        moviePoster = BASE_URL + moviePoster;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //To pass the data to next activity
                    bundle.putString(ORIGINAL_TITLE,original_title);
                    bundle.putString(RELEASE_DATE,release_date);
                    bundle.putString(VOTE_AVERAGE,vote_average);
                    bundle.putString(OVERVIEW,overview);
                    bundle.putString(POSTER_PATH,moviePoster);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie?";
            final String APPID_PARAM = "api_key";
            String movieString = null;

            try
            {
                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendQueryParameter(APPID_PARAM,"b5ffd1855d7011f6147976e2dd84f282")
                    .build();

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line).append("\n");
                }
                movieString = buffer.toString();
                Log.v(LOG_TAG,movieString);
                if (buffer.length() == 0)
                {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                else
                {
                    return movieString;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("MovieTask", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        private String[] getDataFromJSON(String movieString) throws JSONException
        {
            JSONObject posterJSON = new JSONObject(movieString);
            JSONArray posterArray = posterJSON.getJSONArray(RESULT_STRING);

            String[] resultStr = new String[posterArray.length()];

            for(int i = 0; i < posterArray.length(); i++)
            {
                String moviePoster;
                String posterUrl;
                JSONObject movieDetails = posterArray.getJSONObject(i);
                moviePoster = movieDetails.getString(POSTER_PATH);

                posterUrl = BASE_URL + moviePoster;
                Log.v(LOG_TAG,posterUrl);
                resultStr[i] = posterUrl;
            }
            return resultStr;
        }
    }
}
