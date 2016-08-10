package com.firstexample.priyesh.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firstexample.priyesh.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by PRIYESH on 02-08-2016.
 */
public class DetailFragment extends Fragment implements OnPostExecuteOfAsyncTask{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static String movie_id;
    private Cursor mVideoCursor,mReviewCursor;
    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mRecycleAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    private static int flag = 0;
    Button btn;
    String original_title = null, overview = null, release_date = null, vote_average = null, poster_path = null, isFavourite = null;
    ImageView movie_poster;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        //Initialize the recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.video_review_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //To pass reference
        //TODO: check here for detail activity
        OnPostExecuteOfAsyncTask list = new DetailFragment();

        btn = (Button) rootView.findViewById(R.id.movie_favourite_button);
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
        });

        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        String formatted_release_date = null;
        if (bundle != null) {
            isFavourite = bundle.getString(getString(R.string.isFavourite));
            Uri uri;
            Cursor cursor;
            if(isFavourite.equals(getString(R.string.isNoFavourite)))
            {
                movie_id = bundle.getString(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                uri = MovieContract.MovieEntry.buildMovieUriFromId(movie_id);
                cursor = getActivity().getContentResolver().query(uri,
                        null,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{movie_id},
                        null);
            }
            else
            {
                //Remove the Favourites button
                btn.setVisibility(View.GONE);
                movie_id = bundle.getString(MovieContract.FavouriteEntry.COLUMN_MOVIE_ID);
                uri = MovieContract.FavouriteEntry.buildFavouriteMovieUriFromId(movie_id);
                cursor = getActivity().getContentResolver().query(uri,
                        null,
                        MovieContract.FavouriteEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{movie_id},
                        null);
            }
            //As column name of both tables are same, we can go with same cursor
            if(cursor.moveToFirst())
            {
                poster_path = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
                release_date = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
                original_title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));;
                vote_average = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
                formatted_release_date = Utility.getFormattedMonthDay(release_date);
            }
            cursor.close();
        }
        ((TextView) rootView.findViewById(R.id.movie_title)).setText(original_title);
        ((TextView) rootView.findViewById(R.id.movie_synopsis)).setText(overview);
        ((TextView) rootView.findViewById(R.id.movie_release_date)).setText(formatted_release_date);
        ((TextView) rootView.findViewById(R.id.movie_user_rating)).setText(vote_average);

        movie_poster = (ImageView) rootView.findViewById(R.id.movie_poster);
        Picasso.with(getContext()).load(poster_path).into(movie_poster);

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

        mRecycleAdapter = new RecycleAdapter(getContext(), mVideoCursor, mReviewCursor);
        mRecyclerView.setAdapter(mRecycleAdapter);
        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        /*mListViewForVideos = (ListView) findViewById(R.id.listView_youtube_videos);
        mVideoAdapter = new VideoAdapter(this, mVideoCursor, 0);
        mListViewForVideos.setAdapter(mVideoAdapter);*/
        if (mVideoCursor.getCount() == 0)
        {
            FetchVideoTask fetchVideoTask = new FetchVideoTask(getContext(), list);
            fetchVideoTask.execute(movie_id);
        }


        /*mListViewForReviews = (ListView) findViewById(R.id.listView_reviews);
        mReviewAdapter = new ReviewAdapter(this, reviewCursor, 0);
        mListViewForReviews.setAdapter(mReviewAdapter);*/
        if (mReviewCursor.getCount() == 0)
        {
            FetchReviewTask fetchReviewTask = new FetchReviewTask(getContext(), list);
            fetchReviewTask.execute(movie_id);
        }
//        getSupportLoaderManager().initLoader(VIDEO_LOADER, null, this);
//        getSupportLoaderManager().initLoader(REVIEW_LOADER, null, this);

        /*Utility.setDynamicHeight(mListViewForReviews);
        Utility.setDynamicHeight(mListViewForVideos);*/

        //mRecyclerView.setNestedScrollingEnabled(true);
        return rootView;
    }

    public void onAddToFavourites(View view) {
    }


    @Override
    public void afterVideoPostExecute(Context c) {
        Uri videoUri = MovieContract.VideoEntry.buildVideoUriFromId(movie_id);
        mVideoCursor = c.getContentResolver().query(videoUri,
                null,
                MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{movie_id},
                null);
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
    public void updateRecyclerView() {
        Log.v("Flag:",flag+"");
        if(flag >= 1)
        {
            Log.v("Updating Recycler View","Started updating");
            mRecycleAdapter = new RecycleAdapter(getContext(), mVideoCursor, mReviewCursor);
            mRecyclerView.setAdapter(mRecycleAdapter);
            //Log.v("Item Count before:",mRecycleAdapter.getItemCount()+"");
            mRecycleAdapter.notifyDataSetChanged();
            flag = 0;
        }
        else
            flag++;
    }
}
