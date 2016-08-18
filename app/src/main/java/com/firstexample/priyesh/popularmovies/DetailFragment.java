package com.firstexample.priyesh.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firstexample.priyesh.popularmovies.data.MovieContract;

/**
 * Created by PRIYESH on 02-08-2016.
 * Detail fragment
 */
public class DetailFragment extends Fragment implements OnPostExecuteOfAsyncTask{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static String movie_id;
    private Cursor mVideoCursor,mReviewCursor,mMovieCursor;
    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mRecycleAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    private static int flag = 0;
    String isFavourite = null;
    private ShareActionProvider mShareActionProvider;
    private String mForecast;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment,menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mForecast  != null)
        {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }

    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,mForecast);
        return shareIntent;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        setHasOptionsMenu(true);

        //Initialize the recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.video_review_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //To pass reference
        //TODO: check here for detail activity
        OnPostExecuteOfAsyncTask list = new DetailFragment();

        /*btn = (Button) rootView.findViewById(R.id.movie_favourite_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = MovieContract.FavouriteEntry.buildFavouriteMovieUriFromId(movie_id);
                Cursor cur = getActivity().getContentResolver().query(
                        uri,
                        new String[]{MovieContract.FavouriteEntry._ID},
                        MovieContract.FavouriteEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{movie_id},
                        null);
                if(cur.getCount() == 0)
                {
                    Uri fav = MovieContract.FavouriteEntry.CONTENT_URI;
                    ContentValues cv = new ContentValues();
                    cv.put(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID,movie_id);
                    cv.put(MovieContract.FavouriteEntry.COLUMN_ORIGINAL_TITLE,original_title);
                    cv.put(MovieContract.FavouriteEntry.COLUMN_OVERVIEW,overview);
                    cv.put(MovieContract.FavouriteEntry.COLUMN_POSTER_PATH,poster_path);
                    cv.put(MovieContract.FavouriteEntry.COLUMN_RELEASE_DATE,release_date);
                    cv.put(MovieContract.FavouriteEntry.COLUMN_VOTE_AVERAGE,vote_average);

                    Uri resultUri = getActivity().getContentResolver().insert(fav,cv);
                    Toast.makeText(getContext(),"Movie added to favourites",Toast.LENGTH_SHORT).show();
                    Log.v(LOG_TAG + "Data Inserted",resultUri.toString());
                    cur.close();
                }
                else
                {
                    Toast.makeText(getContext(),"Movie already added to favourites",Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        //Intent intent = getActivity().getIntent();
        Bundle bundle = getArguments();
        if(bundle == null) return null;
        if (bundle != null) {
            isFavourite = bundle.getString(getString(R.string.isFavourite));
            Uri uri;
            if(isFavourite.equals(getString(R.string.isNoFavourite)))
            {
                movie_id = bundle.getString(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                uri = MovieContract.MovieEntry.buildMovieUriFromId(movie_id);
                mMovieCursor = getActivity().getContentResolver().query(uri,
                        null,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{movie_id},
                        null);
            }
            else
            {
                //Remove the Favourites button
                //btn.setVisibility(View.GONE);
                movie_id = bundle.getString(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID);
                uri = MovieContract.FavouriteEntry.buildFavouriteMovieUriFromId(movie_id);
                mMovieCursor = getActivity().getContentResolver().query(uri,
                        null,
                        MovieContract.FavouriteEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{movie_id},
                        null);
            }
            //TODO: Have a look on below comment
            //As column name of both tables are same, we can go with same cursor
            /*if(cursor.moveToFirst())
            {
                poster_path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
                release_date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
                original_title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));;
                vote_average = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
                formatted_release_date = Utility.getFormattedMonthDay(release_date);
            }
            cursor.close();*/
        }
        /*((TextView) rootView.findViewById(R.id.movie_title)).setText(original_title);
        ((TextView) rootView.findViewById(R.id.movie_synopsis)).setText(overview);
        ((TextView) rootView.findViewById(R.id.movie_release_date)).setText(formatted_release_date);
        ((TextView) rootView.findViewById(R.id.movie_user_rating)).setText(vote_average);

        movie_poster = (ImageView) rootView.findViewById(R.id.movie_poster);
        Picasso.with(getContext()).load(poster_path).into(movie_poster);*/

        //Get Video Details
        Uri videoUri = MovieContract.VideoEntry.buildVideoUriFromId(movie_id);
        mVideoCursor = getActivity().getContentResolver().query(videoUri,
                null,
                MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{movie_id},
                null);

        //Get Review Details
        Uri reviewUri = MovieContract.ReviewEntry.buildReviewUriFromId(movie_id);
        mReviewCursor = getActivity().getContentResolver().query(reviewUri,
                null,
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{movie_id},
                null);

        mRecycleAdapter = new RecycleAdapter(getContext(), mVideoCursor, mReviewCursor, mMovieCursor);
        mRecyclerView.setAdapter(mRecycleAdapter);
        /*mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        /*mListViewForVideos = (ListView) findViewById(R.id.listView_youtube_videos);
        mVideoAdapter = new VideoAdapter(this, mVideoCursor, 0);
        mListViewForVideos.setAdapter(mVideoAdapter);*/
        if (mVideoCursor.getCount() == 0)
        {
            if (isOnline()) {
                FetchVideoTask fetchVideoTask = new FetchVideoTask(getContext(), list);
                fetchVideoTask.execute(movie_id);
            }
            else
                Toast.makeText(getActivity(),"Network Issue",Toast.LENGTH_SHORT).show();
        }
        else
        {
            mVideoCursor.moveToFirst();
            String key = mVideoCursor.getString(mVideoCursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_VIDEO_KEY));
            String link = String.valueOf(Uri.parse("http://www.youtube.com/watch?v=" + key));
            String desc = "Have a look";
            mForecast = String.format("%s : %s",desc,link);
        }


        /*mListViewForReviews = (ListView) findViewById(R.id.listView_reviews);
        mReviewAdapter = new ReviewAdapter(this, reviewCursor, 0);
        mListViewForReviews.setAdapter(mReviewAdapter);*/
        if (mReviewCursor.getCount() == 0)
        {
            if (isOnline()) {
                FetchReviewTask fetchReviewTask = new FetchReviewTask(getContext(), list);
                fetchReviewTask.execute(movie_id);
            }
            else
                Toast.makeText(getActivity(),"Network Issue",Toast.LENGTH_SHORT).show();
        }

//        getSupportLoaderManager().initLoader(VIDEO_LOADER, null, this);
//        getSupportLoaderManager().initLoader(REVIEW_LOADER, null, this);

        /*Utility.setDynamicHeight(mListViewForReviews);
        Utility.setDynamicHeight(mListViewForVideos);*/

        //mRecyclerView.setNestedScrollingEnabled(false);
        return rootView;

    }


    @Override
    public void afterVideoPostExecute(Context c) {
        Uri videoUri = MovieContract.VideoEntry.buildVideoUriFromId(movie_id);
        mVideoCursor = c.getContentResolver().query(videoUri,
                null,
                MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{movie_id},
                null);
        if (mVideoCursor != null)
        {
            mVideoCursor.moveToFirst();
            String key = mVideoCursor.getString(mVideoCursor.getColumnIndex(MovieContract.VideoEntry.COLUMN_VIDEO_KEY));
            mForecast = String.valueOf(Uri.parse("http://www.youtube.com/watch?v=" + key));
        }
    }

    @Override
    public void afterReviewPostExecute(Context c) {
        Uri reviewUri = MovieContract.ReviewEntry.buildReviewUriFromId(movie_id);
        mReviewCursor = c.getContentResolver().query(reviewUri,
                null,
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{movie_id},
                null);
        //mRecycleAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateRecyclerView(Context c) {
        Log.v("Flag:",flag+"");
        if(flag >= 1)
        {
            Uri uri = MovieContract.MovieEntry.buildMovieUriFromId(movie_id);
            mMovieCursor = c.getContentResolver().query(uri,
                    null,
                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                    new String[]{movie_id},
                    null);
            Log.v("Updating Recycler View","Started updating");
            mRecycleAdapter = new RecycleAdapter(c, mVideoCursor, mReviewCursor, mMovieCursor);
            mRecyclerView.setAdapter(mRecycleAdapter);
            //Log.v("Item Count before:",mRecycleAdapter.getItemCount()+"");
            mRecycleAdapter.notifyDataSetChanged();
            flag = 0;
        }
        else
            flag++;
    }

    //To check for internet connection
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
