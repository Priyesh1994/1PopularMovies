package com.firstexample.priyesh.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.firstexample.priyesh.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by PRIYESH on 29-07-2016.
 */
public class FetchMovieTask extends AsyncTask<String,Void,String> {

    //Reason of not using MovieEntry here is because this String are there to retrieve data from cloud.
    //The retrieved JSON may have different key name than with the fields of the table.
    final String MOVIE_ID = "id";
    final String POSTER_PATH = "poster_path";
    final String RELEASE_DATE = "release_date";
    final String OVERVIEW = "overview";
    final String ORIGINAL_TITLE = "original_title";
    final String VOTE_AVERAGE = "vote_average";
    final String RESULT_STRING = "results";
    final String BASE_URL = "http://image.tmdb.org/t/p/w185/";

    private static final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private String movie_id;
    private String[] movie_id_for_video;

    private final Context mContext;

    private MainActivity mainActivity;

    public FetchMovieTask(Context mContext, MainActivity activity) {
        this.mContext = mContext;
        mainActivity = activity;
    }

    @Override
    protected void onPostExecute(final String movieString) {
        GridView view = (GridView) mainActivity.findViewById(R.id.movies_grid);
        String[] posterArray = new String[0];
        try {
            posterArray = getDataFromJSON(movieString);
            JSONObject posterJSON = new JSONObject(movieString);
            JSONArray movieArray = posterJSON.getJSONArray(RESULT_STRING);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());
            //movie_id_for_video = new String[movieArray.length()];

            for(int position = 0; position < movieArray.length(); position++)
            {
                JSONObject movieDetails = movieArray.getJSONObject(position);

                //To retrieve details of the selected movie
                movie_id = movieDetails.getString(MOVIE_ID);
                //movie_id_for_video[position] = movie_id;
                String moviePoster = movieDetails.getString(POSTER_PATH);
                String release_date = movieDetails.getString(RELEASE_DATE);
                String overview = movieDetails.getString(OVERVIEW);
                String original_title = movieDetails.getString(ORIGINAL_TITLE);
                String vote_average = movieDetails.getString(VOTE_AVERAGE);
                moviePoster = BASE_URL + moviePoster;

                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movie_id);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,moviePoster);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,release_date);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,overview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,original_title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,vote_average);

                cVVector.add(movieValues);
            }

            int rowsInserted = 0;
            if(cVVector.size() > 0)
            {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                rowsInserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        view.setAdapter(new ImageAdapter(mContext,posterArray));

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(mContext,DetailActivity.class);
                Bundle bundle = new Bundle();
                JSONObject posterJSON = null;
                try {
                    posterJSON = new JSONObject(movieString);
                    JSONArray movieArray = posterJSON.getJSONArray(RESULT_STRING);
                    JSONObject movieDetails = movieArray.getJSONObject(position);

                    //To retrieve id of the selected movie
                    movie_id = movieDetails.getString(MOVIE_ID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //To pass the data to next activity
                bundle.putString(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movie_id);
                bundle.putString(mContext.getString(R.string.isFavourite),
                        mContext.getString(R.string.isNoFavourite));
                intent.putExtras(bundle);
                mainActivity.startActivity(intent);
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
            //Log.v(LOG_TAG,movieString);
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
            //Log.v(LOG_TAG,posterUrl);
            resultStr[i] = posterUrl;
        }
        return resultStr;
    }
}
