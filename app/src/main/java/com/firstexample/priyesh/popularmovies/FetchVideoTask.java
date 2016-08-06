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
    private OnPostExecuteOfAsyncTask listener;
    private DetailActivity detailActivity;
    final String BASE_URI = "http://img.youtube.com/vi/";
    final String IMG_PATH = "0.jpg";

    public FetchVideoTask(Context mContext, DetailActivity detailActivity,OnPostExecuteOfAsyncTask listener) {
        this.mContext = mContext;
        this.detailActivity = detailActivity;
        this.listener = listener;
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
        //LinearLayout mLinearLayout = (LinearLayout) detailActivity.findViewById(R.id.detail_layout);

        try {
            JSONObject object = new JSONObject(s);
            String movieId = object.getString("id");
            JSONArray resultsArray = object.getJSONArray("results");
            Vector<ContentValues> cVVector = new Vector<ContentValues>(resultsArray.length());
            for (int position = 0; position < resultsArray.length(); position++)
            {
                JSONObject videoObject = resultsArray.getJSONObject(position);
                /*View videoItem = LayoutInflater.from(detailActivity).inflate(R.layout.video_item,null);
                ImageView iconView = (ImageView) videoItem.findViewById(R.id.list_image_video);*/

                ContentValues videoValues = new ContentValues();
                videoValues.put(MovieContract.VideoEntry.COLUMN_MOVIE_ID,movieId);
                videoValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_ID,videoObject.getString("id"));
                videoValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_KEY,videoObject.getString("key"));
                videoValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_TYPE,videoObject.getString("type"));
                videoValues.put(MovieContract.VideoEntry.COLUMN_VIDEO_NAME,videoObject.getString("name"));
                cVVector.add(videoValues);
                /*((TextView)videoItem.findViewById(R.id.list_text_video)).setText(videoObject.getString("name"));
                final String key = videoObject.getString("key");
                Uri builtThumbUri = Uri.parse(BASE_URI).buildUpon()
                        .appendPath(key)
                        .appendPath(IMG_PATH)
                        .build();
                Picasso.with(mContext).load(builtThumbUri).into(iconView);
                videoItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }catch (ActivityNotFoundException ex) {
                            Intent intent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://www.youtube.com/watch?v=" + key));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                        }
                    }
                });
                mLinearLayout.addView(videoItem);
                Log.v("In FetchVideoTask:","Added");*/
            }
            int rowsInserted = 0;
            if(cVVector.size() > 0)
            {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                rowsInserted = mContext.getContentResolver().bulkInsert(MovieContract.VideoEntry.CONTENT_URI, cvArray);
                Log.v("Rows InsertedInVideo: ",(rowsInserted+""));
                listener.afterVideoPostExecute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
