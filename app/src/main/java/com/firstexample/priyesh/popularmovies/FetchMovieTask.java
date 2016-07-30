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
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Vector;

/**
 * Created by PRIYESH on 29-07-2016.
 */
public class FetchMovieTask extends AsyncTask<String,Void,String> {

    final String MOVIE_ID = "id";
    final String POSTER_PATH = "poster_path";
    final String RELEASE_DATE = "release_date";
    final String OVERVIEW = "overview";
    final String ORIGINAL_TITLE = "original_title";
    final String VOTE_AVERAGE = "vote_average";
    final String RESULT_STRING = "results";
    final String BASE_URL = "http://image.tmdb.org/t/p/w185/";

    private static final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private String overview,release_date,vote_average,original_title,moviePoster,movie_id;

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
        JSONObject posterJSON = null;
        try {
            posterArray = getDataFromJSON(movieString);

            posterJSON = new JSONObject(movieString);
            JSONArray movieArray = posterJSON.getJSONArray(RESULT_STRING);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for(int position = 0; position < movieArray.length(); position++)
            {
                JSONObject movieDetails = movieArray.getJSONObject(position);

                //To retrieve details of the selected movie
                movie_id = movieDetails.getString(MOVIE_ID);
                moviePoster = movieDetails.getString(POSTER_PATH);
                release_date = movieDetails.getString(RELEASE_DATE);
                overview = movieDetails.getString(OVERVIEW);
                original_title = movieDetails.getString(ORIGINAL_TITLE);
                vote_average = movieDetails.getString(VOTE_AVERAGE);
                moviePoster = BASE_URL + moviePoster;

                String formatted_release_date = formatDate(release_date);

                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movie_id);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,moviePoster);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,formatted_release_date);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,overview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,original_title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,vote_average);

                cVVector.add(movieValues);
            }

            int rowsInserted = 0;
            int rowsDeleted = 0;
            if(cVVector.size() > 0)
            {
                //rowsDeleted = mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
                //Log.v(LOG_TAG + "Rows Deleted:",rowsDeleted + "");

                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                rowsInserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
                Log.v(LOG_TAG + "Rows Inserted:",rowsInserted + "");
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

                //To pass the data to next activity
                bundle.putString(ORIGINAL_TITLE,original_title);
                bundle.putString(RELEASE_DATE,release_date);
                bundle.putString(VOTE_AVERAGE,vote_average);
                bundle.putString(OVERVIEW,overview);
                bundle.putString(POSTER_PATH,moviePoster);
                intent.putExtras(bundle);
                mainActivity.startActivity(intent);
            }
        });

    }

    private String formatDate(String release_date){
        //String someDate = "1995-01-01";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(release_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        release_date = date.getTime() + "";
        Log.v("Date format:", release_date);
        return release_date;
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
