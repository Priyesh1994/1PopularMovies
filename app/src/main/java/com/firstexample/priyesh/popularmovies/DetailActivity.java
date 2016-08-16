package com.firstexample.priyesh.popularmovies;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

//implements LoaderManager.LoaderCallbacks<Cursor>
//implements OnPostExecuteOfAsyncTask
public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final int VIDEO_LOADER = 0;
    private static final int REVIEW_LOADER = 1;
    private Cursor mVideoCursor,mReviewCursor;
    private static RecyclerView mRecyclerView;
    private static RecyclerView.Adapter mRecycleAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    private static int flag = 0;

    Button btn;
    private static String movie_id;
    String original_title = null, overview = null, release_date = null, vote_average = null, poster_path = null, isFavourite = null;

    ImageView movie_poster;
    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;
    private ListView mListViewForVideos;
    private ListView mListViewForReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if(savedInstanceState == null )
        {
            Bundle bundle = getIntent().getExtras();
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_movie_container,fragment)
                    .commit();
        }

        /*mRecyclerView = (RecyclerView) findViewById(R.id.video_review_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        OnPostExecuteOfAsyncTask list = new DetailActivity();

        btn = (Button) findViewById(R.id.movie_favourite_button);

        Intent intent = getIntent();
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
                cursor = getContentResolver().query(uri,
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
                cursor = getContentResolver().query(uri,
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
        ((TextView) findViewById(R.id.movie_title)).setText(original_title);
        ((TextView) findViewById(R.id.movie_synopsis)).setText(overview);
        ((TextView) findViewById(R.id.movie_release_date)).setText(formatted_release_date);
        ((TextView) findViewById(R.id.movie_user_rating)).setText(vote_average);

        movie_poster = (ImageView) findViewById(R.id.movie_poster);
        Picasso.with(this).load(poster_path).into(movie_poster);

        //Get Video Details
        Uri videoUri = MovieContract.VideoEntry.buildVideoUriFromId(movie_id);
        mVideoCursor = getContentResolver().query(videoUri,
                null,
                MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{movie_id},
                null);

        //Get Review Details
        Uri reviewUri = MovieContract.ReviewEntry.buildReviewUriFromId(movie_id);
        mReviewCursor = getContentResolver().query(reviewUri,
                null,
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{movie_id},
                null);

        mRecycleAdapter = new RecycleAdapter(this, mVideoCursor, mReviewCursor);
        mRecyclerView.setAdapter(mRecycleAdapter);
        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        *//*mListViewForVideos = (ListView) findViewById(R.id.listView_youtube_videos);
        mVideoAdapter = new VideoAdapter(this, mVideoCursor, 0);
        mListViewForVideos.setAdapter(mVideoAdapter);*//*
        if (mVideoCursor.getCount() == 0)
        {
            FetchVideoTask fetchVideoTask = new FetchVideoTask(getApplicationContext(), list);
            fetchVideoTask.execute(movie_id);
        }


        *//*mListViewForReviews = (ListView) findViewById(R.id.listView_reviews);
        mReviewAdapter = new ReviewAdapter(this, reviewCursor, 0);
        mListViewForReviews.setAdapter(mReviewAdapter);*//*
        if (mReviewCursor.getCount() == 0)
        {
            FetchReviewTask fetchReviewTask = new FetchReviewTask(getApplicationContext(), list);
            fetchReviewTask.execute(movie_id);
        }
//        getSupportLoaderManager().initLoader(VIDEO_LOADER, null, this);
//        getSupportLoaderManager().initLoader(REVIEW_LOADER, null, this);

        *//*Utility.setDynamicHeight(mListViewForReviews);
        Utility.setDynamicHeight(mListViewForVideos);*/
    }

    /*public void onAddToFavourites(View view) {
        Uri uri = MovieContract.FavouriteEntry.buildFavouriteMovieUriFromId(movie_id);
        Cursor cur = getContentResolver().query(
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

            Uri resultUri = getContentResolver().insert(fav,cv);
            Toast.makeText(this,"Movie added to favourites",Toast.LENGTH_SHORT).show();
            Log.v(LOG_TAG + "Data Inserted",resultUri.toString());
            cur.close();
        }
        else
        {
            Toast.makeText(this,"Movie already added to favourites",Toast.LENGTH_SHORT).show();
        }
    }*/

/*    @Override
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
            mRecycleAdapter = new RecycleAdapter(this, mVideoCursor, mReviewCursor);
            mRecyclerView.setAdapter(mRecycleAdapter);
            //Log.v("Item Count before:",mRecycleAdapter.getItemCount()+"");
            mRecycleAdapter.notifyDataSetChanged();
            flag = 0;
        }
        else
            flag++;
    }*/
/*@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id)
        {
            case VIDEO_LOADER:
                Uri videoUri = MovieContract.VideoEntry.buildVideoUriFromId(movie_id);

                return new CursorLoader(this,
                        videoUri,
                        null,
                        MovieContract.VideoEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{movie_id},
                        null);

            case REVIEW_LOADER:
                Uri reviewUri = MovieContract.ReviewEntry.buildReviewUriFromId(movie_id);

                return new CursorLoader(this,
                        reviewUri,
                        null,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{movie_id},
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId())
        {
            case VIDEO_LOADER:
                mVideoAdapter.swapCursor(data);
                break;
            case REVIEW_LOADER:
                mReviewAdapter.swapCursor(data);
                break;
        }
//        Utility.setDynamicHeight(mListViewForVideos);
//        Utility.setDynamicHeight(mListViewForReviews);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId())
        {
            case VIDEO_LOADER:
                mVideoAdapter.swapCursor(null);
                break;
            case REVIEW_LOADER:
                mReviewAdapter.swapCursor(null);
                break;
        }
    }*/
}