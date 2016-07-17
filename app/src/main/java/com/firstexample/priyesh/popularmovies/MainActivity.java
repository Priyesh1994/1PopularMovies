package com.firstexample.priyesh.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        //String[] posterArray = null;
        fetchMovieTask.execute("popular");
    }

    public class FetchMovieTask extends AsyncTask<String,Void,String[]>{

        @Override
        protected void onPostExecute(String[] posterArray) {
            //super.onPostExecute(strings);
            GridView view = (GridView) findViewById(R.id.movies_grid);
            view.setAdapter(new ImageAdapter(MainActivity.this,posterArray));

            view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(MainActivity.this,LOG_TAG,Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        protected String[] doInBackground(String... params) {

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

                //Log.v(LOG_TAG,url.toString());
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
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
                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                else {
                    try {
                        return getPosterFromJSON(movieString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

        private String[] getPosterFromJSON(String movieString) throws JSONException
        {
            final String RESULT_STRING = "results";
            final String POSTER_PATH = "poster_path";
            final String BASE_URL = "http://image.tmdb.org/t/p/w185/";

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
