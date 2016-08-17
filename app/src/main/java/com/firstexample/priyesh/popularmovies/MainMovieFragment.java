package com.firstexample.priyesh.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.firstexample.priyesh.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by PRIYESH on 08-08-2016.
 */
public class MainMovieFragment extends Fragment {

    private static final String LOG_TAG = MainMovieFragment.class.getSimpleName();
    private String first_sort_order;
    private Cursor cur;
    private static View mRootView;
    private String[] mPosterArray;

    final String RESULT_STRING = "results";
    final String BASE_URL = "http://image.tmdb.org/t/p/w185/";
    final String POSTER_PATH = "poster_path";
    final String MOVIE_ID = "id";
    private String mMovieString;
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    public MainMovieFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public interface CallBack
    {
        public void onItemSelected(Bundle bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_main_movie, container, false);
        mGridView = (GridView) mRootView.findViewById(R.id.movies_grid);
        //mGridView.setBa
        updateMovies();
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            mGridView.setSelection(mPosition);
            long POST_MS_DELAY = 500;
            mGridView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int offset = 200;
                    int SMOOTH_SCROLL_MS_DURATION = 1000;
                    mGridView.smoothScrollToPositionFromTop(mPosition, offset, SMOOTH_SCROLL_MS_DURATION);
                }
            }, POST_MS_DELAY);
            //mGridView.smoothScrollToPosition(mPosition);
        }
        /*if(savedInstanceState != null && mPosition != GridView.INVALID_POSITION)
        {
            mGridView.smoothScrollToPosition(mPosition);
            //mGridView.smoothScrollByOffset(mPosition);
        }*/
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != GridView.INVALID_POSITION)
            outState.putInt(SELECTED_KEY,mPosition);
        super.onSaveInstanceState(outState);
    }

    public void updateMovies() {
        if(isOnline())
        {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            first_sort_order = prefs.getString(getString(R.string.sort_key),
                    getString(R.string.pref_sort_popular));
            if(!first_sort_order.equals(getString(R.string.pref_sort_favourites)))
            {
                Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
                cur = getActivity().getContentResolver().query(movieUri, null, null, null, null);
                mPosterArray = getCursorArray(cur);
                if (cur.getCount() > 0) {
                    mGridView.setAdapter(new ImageAdapter(getContext(), mPosterArray));
                } else {
                    FetchMovieTask fetchMovieTask = new FetchMovieTask(getContext());
                    try {
                        mMovieString = fetchMovieTask.execute(first_sort_order).get();
                        mPosterArray = getDataFromJSON(mMovieString);
                    } catch (InterruptedException | ExecutionException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else
            {
                Uri uri = MovieContract.FavouriteEntry.CONTENT_URI;
                cur = getActivity().getContentResolver().query(uri,null,null,null,null);
                mPosterArray = getCursorArray(cur);
            }
            mGridView.setAdapter(new ImageAdapter(getContext(), mPosterArray));
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                public String movie_id;

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    cur.moveToPosition(position);
                    //Intent intent = new Intent(getActivity(), DetailActivity.class);
                    Bundle bundle = new Bundle();
                    if (cur.getCount() > 0) {
                        if (!first_sort_order.equals(getString(R.string.pref_sort_favourites))) {
                            bundle.putString(MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                                    cur.getString(cur.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
                            bundle.putString(getString(R.string.isFavourite),
                                    getString(R.string.isNoFavourite));
                        } else {
                            bundle.putString(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID,
                                    cur.getString(cur.getColumnIndex(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID)));
                            bundle.putString(getString(R.string.isFavourite),
                                    getString(R.string.isYesFavourite));
                        }
                    } else
                    {
                        JSONObject posterJSON = null;
                        try {
                            posterJSON = new JSONObject(mMovieString);
                            JSONArray movieArray = posterJSON.getJSONArray(RESULT_STRING);
                            JSONObject movieDetails = movieArray.getJSONObject(position);

                            //To retrieve id of the selected movie
                            movie_id = movieDetails.getString(MOVIE_ID);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //To pass the data to next activity
                        bundle.putString(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movie_id);
                        bundle.putString(getString(R.string.isFavourite),
                                getString(R.string.isNoFavourite));
                    }
                    ((CallBack) getActivity()).onItemSelected(bundle);
                    //intent.putExtras(bundle);
                    //startActivity(intent);
                    mPosition = position;
                    mGridView.setSelection(mPosition);
                    //mGridView.setBackgroundColor();
                    //mGridView.setBackgroundResource(R.drawable.grid_selector);
                }
            });
        }
        else
        {
            Toast.makeText(getActivity(),"Network Issue",Toast.LENGTH_SHORT).show();
            Log.v(LOG_TAG,"Network error");
        }
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

    private String[] getCursorArray(Cursor cur) {
        String[] posterArray = new String[cur.getCount()];
        if (cur.moveToFirst()) {
            int i = 0;
            do {
                posterArray[i] = cur.getString(cur.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
                i++;
            } while (cur.moveToNext());
        }
        return posterArray;
    }
/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mainactivity,menu);
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.settings)
        {
            startActivity(new Intent(getActivity(),Settings.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    //To check for internet connection
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
