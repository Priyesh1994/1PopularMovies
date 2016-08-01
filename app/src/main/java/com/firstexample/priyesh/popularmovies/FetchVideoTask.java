package com.firstexample.priyesh.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by PRIYESH on 31-07-2016.
 */
public class FetchVideoTask extends AsyncTask<String,Void,String> {

    private final Context mContext;

    public FetchVideoTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... params) {
        String movie_id = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie?";
        final String APPID_PARAM = "api_key";

        String movieString = null;
        try {
            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(movie_id)
                    .appendPath("videos")
                    .appendQueryParameter(APPID_PARAM, "b5ffd1855d7011f6147976e2dd84f282")
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
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
        return movieString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject object = new JSONObject(s);
            String movieId = object.getString("id");
            JSONArray resultsArray = object.getJSONArray("results");
            Vector<ContentValues> cVVector = new Vector<ContentValues>(resultsArray.length());
            for (int position = 0; position < resultsArray.length(); position++)
            {
                JSONObject videoObject = resultsArray.getJSONObject(position);

                ContentValues videoValues = new ContentValues();
                videoValues.put(MovieContract.VideoEntry.COLUMN_MOVIE_ID,movieId);
                videoValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_ID,videoObject.getString("id"));
                videoValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_KEY,videoObject.getString("key"));
                videoValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_TYPE,videoObject.getString("type"));
                videoValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_NAME,videoObject.getString("name"));
                cVVector.add(videoValues);
            }
            int rowsInserted = 0;
            if(cVVector.size() > 0)
            {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                rowsInserted = mContext.getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, cvArray);
                Log.v("Rows InsertedInVideo: ",(rowsInserted+""));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}